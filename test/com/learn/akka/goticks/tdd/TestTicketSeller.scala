package com.learn.akka.goticks.tdd

import akka.actor.{Actor, ActorRef, Props}
import akka.goticks.tdd.TicketSeller
import akka.goticks.tdd.TicketSeller._
import akka.testkit.TestActorRef
import com.learn.akka.AkkaTestSpec

//For test only, expose the internal state.
class TicketSellerActor extends TicketSeller
{
  def getTickets = tickets
}

class Kiosk(nextKiosk: ActorRef) extends Actor {
  override def receive = {
    case ticket @ Ticket(_, number) =>
      nextKiosk ! ticket
  }
}

class TestTicketSeller extends AkkaTestSpec{

  //single-thread version
  "TicketSeller" should "change its internal state when it receives a Ticket Message" in {
    val seller = TestActorRef[TicketSellerActor](Props[TicketSellerActor], "seller")
    val tickets = Ticket("NBA", 10) :: Nil
    seller ! Tickets(tickets)

    seller.underlyingActor.getTickets should equal(tickets)
  }

  //multi-thread version
  it should "return the correct ticket number when receives a GetTicketNumber Message" in {
    val seller = TestActorRef[TicketSeller]
    val tickets = Ticket("NBA", 10) :: Nil
    seller ! Tickets(tickets)

    seller ! GetTicketNumber
    expectMsg(1)
  }

  //sending actor
  "Kiosk" should "foward the Ticket message to next one" in {
    val kiosk = system.actorOf(Props(new Kiosk(testActor)))

    kiosk ! Ticket("NBA", 100)

    expectMsgPF() {
      case ticket @ Ticket(_, number) => number shouldBe 100
    }
  }

}
