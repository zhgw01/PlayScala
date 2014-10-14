package com.learn.akka.goticks

import akka.actor.Props
import akka.goticks.TicketSeller
import akka.goticks.TicketSeller._
import akka.testkit.TestActorRef
import com.learn.akka.AkkaTestSpec

//For test only, expose the internal state.
class TicketSellerActor extends TicketSeller
{
  def getTickets = tickets
}

class TestTicketSeller extends AkkaTestSpec{

  "TicketSeller" should "change its internal state when it receive a Ticket Message" in {
    val seller = TestActorRef[TicketSellerActor](Props[TicketSellerActor], "seller")
    val tickets = Ticket("NBA", 10) :: Nil
    seller ! Tickets(tickets)

    seller.underlyingActor.getTickets should equal(tickets)
  }

}
