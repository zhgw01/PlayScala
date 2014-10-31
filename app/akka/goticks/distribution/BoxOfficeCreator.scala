package akka.goticks.distribution

import akka.actor.{Props, ActorRef, Actor}
import akka.goticks.tdd.BoxOffice

trait BoxOfficeCreator { this: Actor =>
  def createBoxOffice: ActorRef = {
    context.actorOf(Props[BoxOffice], "boxOffice")
  }
}
