package controllers

import akka.actor.{Props, Actor}
import akka.pattern.ask
import akka.util.Timeout
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.iteratee.{Enumerator, Iteratee, Concurrent}
import play.api.mvc.{WebSocket, Action, Controller}
import scala.language.postfixOps
import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext.Implicits.global

object Chat extends Controller{

  implicit val timeout = Timeout(1 seconds)
  val room = Akka.system.actorOf(Props[ChatRoom])

  def showRoom(nick: String) = Action {
    implicit request =>
      Ok(views.html.chat.ShowRoom(nick))
  }

  def chatSocket(nick: String) = WebSocket.tryAccept[String]{
    implicit request =>
      val channelsFuture = room ? Join(nick)

    channelsFuture.mapTo[Either[play.api.mvc.Result, (Iteratee[String, _], Enumerator[String])]]
  }

  case class Join(nick: String)
  case class Leave(nick: String)
  case class Broadcast(message: String)


  class ChatRoom extends Actor {
    var users = Set[String]()
    val (enumerator, channel) = Concurrent.broadcast[String]

    override def receive = {
      case Join(nick) =>
        if (!users.contains(nick)) {
          val iteratee = Iteratee.foreach[String] {
            message => self ! Broadcast(s"$nick: $message")
          }.map {
            _ => self ! Leave(nick)
          }

          users += nick
          channel.push(s"User $nick join the room, now ${users.size} users")
          sender ! Right(iteratee, enumerator)
        } else {

          val enumerator = Enumerator(s"Nickname $nick is already in use")
          val iteratee = Iteratee.ignore
          sender ! Right(iteratee, enumerator)
        }

      case Leave(nick) =>
        users -= nick
        channel.push(s"User $nick has left the room, ${users.size} users left")

      case Broadcast(msg: String) => channel.push(msg)
    }
  }

}
