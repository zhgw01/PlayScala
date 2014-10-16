package akka.goticks.faulttolerance

import java.io.File

import scala.concurrent.duration._

import akka.actor.SupervisorStrategy._
import akka.actor._

object LogPrcoessing {
  case class LogFile(file: File)
  case class Line(time: Long, message: String, messageType: String)

  case class NewFile(file: File, timeAdded: Long)
  case class SourceAbandoned(uri: String)
}

trait LogParsing {
  import LogPrcoessing._
  def parse(file: File): Seq[Line] = {
    Nil
  }
}

trait FileWatchingAbilities {
  def register(uri: String): Unit = {

  }
}

//exceptions
@SerialVersionUID(1L)
class DiskError(msg: String) extends Error(msg) with Serializable

@SerialVersionUID(1L)
class CorruptedFileException(msg: String, val file: File) extends Exception(msg) with Serializable

@SerialVersionUID(1L)
class DbBrokenConnectionException(msg: String) extends Exception(msg) with Serializable


class DbCon(url: String) {
  def write(map: Map[Symbol, Any]): Unit = {

  }
}

//DB Actor
class DbWriter(connection: DbCon) extends Actor {
  import LogPrcoessing._

  override def receive = {
    case Line(time, message, messageType) =>
      connection.write(Map('time -> time,
        'message -> message,
        'messageType -> messageType))
  }
}

class DbSupervisor(writerProps: Props) extends Actor {
  override def supervisorStrategy = OneForOneStrategy() {
    //this is the implementation of decider by using curly function
    case _: DbBrokenConnectionException => Restart
  }

  val writer = context.actorOf(writerProps)

  override def receive = {
    case m => writer forward m
  }
}

class DbImpatientSupervisor(writerProps: Props) extends Actor {
  override def supervisorStrategy = OneForOneStrategy(
    maxNrOfRetries = 5,
    withinTimeRange = 30 seconds) {
    case _: DbBrokenConnectionException => Restart
  }

  val writer = context.actorOf(writerProps)

  override def receive: Receive = {
    case m => writer forward m
  }
}

//LogProcessor Actor
class LogProcessor(dbSupervisor: ActorRef) extends Actor with LogParsing{
  import LogPrcoessing._

  override def receive: Receive = {
    case LogFile(file) =>
      val lines = parse(file)
      lines.foreach(dbSupervisor ! _)
  }
}

class LogProcSupervisor(dbSupervisorProps: Props) extends Actor {

  override def supervisorStrategy = OneForOneStrategy(){
    case _: CorruptedFileException => Resume //ignore the error file
  }

  val dbSupervisor = context.actorOf(dbSupervisorProps)
  val logProcessor = context.actorOf(Props(new LogProcessor(dbSupervisor)))

  override def receive: Receive = {
    case m => logProcessor forward m
  }
}

//FileWatcher Actor
class FileWatcher(sourceUri: String, logProcSupervisor: ActorRef) extends Actor with FileWatchingAbilities{
  import LogPrcoessing._

  register(sourceUri)

  override def receive: Actor.Receive = {
    case NewFile(file, _) => logProcSupervisor ! LogFile(file)
    case SourceAbandoned(uri) if uri == sourceUri => self ! PoisonPill
  }
}

class FileWatcherSupervisor(sources: Vector[String], logProcSuperProps: Props) extends Actor {
  override def supervisorStrategy = AllForOneStrategy() {
    case _: DiskError => Stop
  }

  var fileWatchers: Vector[ActorRef] = sources map { source =>
    val logProcSupervisor = context.actorOf(logProcSuperProps)
    val fileWatcher = context.actorOf(Props(new FileWatcher(source, logProcSupervisor)))
    context.watch(fileWatcher)
    fileWatcher
  }

  override def receive: Actor.Receive = {
    case Terminated(fileWatcher) =>
      fileWatchers = fileWatchers.filterNot(w => w == fileWatcher)
      if(fileWatchers.isEmpty) self ! PoisonPill
  }
}