package akka.goticks.tdd

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class BoxOffice extends Actor with ActorLogging{
  import akka.goticks.tdd.TicketSeller._
  implicit val timeout = Timeout(5 seconds)

  def createTicketSeller(name: String) = context.actorOf(Props[TicketSeller], name)

  override def receive: Receive = {
    case Event(name, nrOfTickets) =>
      log.info(s"create new event $name with $nrOfTickets tickets")

      if(context.child(name).isEmpty) {
        val seller = createTicketSeller(name)
        val tickets = Tickets((1 to nrOfTickets).map(nr => Ticket(name, nr)).toList)

        seller ! tickets
      }

    case TicketRequest(name) =>
      log.info(s"Getting a ticket for the $name event")

      context.child(name) match {
        case Some(seller) => seller.forward(BuyTicket)
        case None => sender ! SoldOut
      }

    case GetEvents =>
      import akka.pattern.ask

      val captureSender = sender

      def askAndMapToEvent(seller: ActorRef) = {
        val futureTicketNumber = (seller ? GetTicketNumber).mapTo[Int]
        futureTicketNumber.map{ number =>
          Event(seller.actorRef.path.name, number)
        }
      }

      val futureEvents = context.children.map { seller =>
        askAndMapToEvent(seller)
      }

      Future.sequence(futureEvents).map { events =>
        captureSender ! Events(events.toList)
      }
  }
}
