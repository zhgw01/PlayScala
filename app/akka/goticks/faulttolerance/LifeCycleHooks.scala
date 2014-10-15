package akka.goticks.faulttolerance

import akka.actor.{Actor, ActorLogging}

class LifeCycleHooks extends Actor with ActorLogging{

  log.info("Constructor")

  override def preStart(): Unit = {
    log.info("preStart")
  }

  override def postStop(): Unit = {
    log.info("postStop")
  }

  override def preRestart(reason: Throwable,
                          message: Option[Any]): Unit = {
    log.info("preRestart")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    log.info("postRestart")
    super.postRestart(reason)
  }

  override def receive = {
    case "restart" =>
      throw new IllegalStateException("force restart")

    case msg: AnyRef =>
      log.info("Receive")
      sender ! msg
  }

}
