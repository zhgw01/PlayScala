package akka.goticks.fsm

import akka.actor.{ActorRef, FSM, Actor}

//event
case class BookRequest(context: AnyRef, target: ActorRef)
case class BookSupply(nrBooks: Int)
case object BookSupplySoldOut
case object Done
case object PendingRequests

//state
sealed trait State
case object WaitForRequest extends State
case object ProcessRequest extends State
case object WaitForPublisher extends State
case object SoldOut extends State
case object ProcessSoldOut extends State

case class StateData(nrBooksInStore: Int, pendingRequests:Seq[BookRequest])

class Inventory extends Actor with FSM[State, StateData]{
  startWith(WaitForRequest, new StateData(0, Seq()))

  when(WaitForRequest) {
    case Event(request: BookRequest, data: StateData) => {
      val newStateData = data.copy(pendingRequests = data.pendingRequests :+ request)
      if(newStateData.nrBooksInStore > 0)
        goto(ProcessRequest) using newStateData
      else
        goto(WaitForPublisher) using newStateData
    }

    case Event(PendingRequests, data: StateData) => {
      if(data.pendingRequests.isEmpty) {
        stay
      } else if(data.nrBooksInStore > 0) {
        goto(ProcessRequest)
      } else {
        goto(WaitForPublisher)
      }
    }
  }


  whenUnhandled {
    case Event(request: BookRequest, data: StateData) => {
      stay using data.copy(pendingRequests = data.pendingRequests :+ request)
    }

    case Event(e, s) => {
      log.warning("receive unhandled request {} in state {}/{}", e, stateName, s)
      stay
    }
  }

}
