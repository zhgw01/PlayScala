package akka.goticks.tdd

import akka.actor.{Actor, PoisonPill}

object TicketSeller {
  case object GetEvents
  case object EventCreated
  case object BuyTicket
  case object SoldOut
  case object GetTicketNumber

  case class Event(event:String, nrOfTickets:Int)
  case class Events(events:List[Event])
  case class Ticket(event:String, nr:Int)
  case class Tickets(tickets:List[Ticket])
  case class TicketRequest(event:String)
}

class TicketSeller extends Actor{
  import akka.goticks.tdd.TicketSeller._

  var tickets = Vector[Ticket]()

  override def receive = {
    case GetTicketNumber =>
      sender ! tickets.size

    case Tickets(newTickets) =>
      tickets = tickets ++ newTickets

    case BuyTicket =>
      if (tickets.isEmpty) {
        sender ! SoldOut
        self ! PoisonPill
      }

      tickets.headOption.foreach { ticket =>
        sender ! ticket
        tickets = tickets.tail
      }
  }
}
