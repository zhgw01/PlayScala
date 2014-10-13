package controllers.goticks

import akka.actor.Props
import akka.goticks.BoxOffice
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.libs.concurrent.Akka
import play.api.mvc.{Action, Controller}
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


object BoxOfficeController extends Controller{
  import akka.goticks.TicketSeller._
  implicit val timeout = Timeout(5 seconds)

  val boxOfficeActor = Akka.system.actorOf(Props[BoxOffice], "BoxOffice")

  val eventForm = Form(mapping(
    "event" -> of[String],
    "nrOfTickets" -> of[Int]
  )(Event.apply)(Event.unapply))


  def events = Action.async {
    import akka.pattern.ask
    val futureEvents = (boxOfficeActor ? GetEvents).mapTo[Events]
    futureEvents.map {
      eventList =>
        Ok(views.html.goticks.events(eventList.events))
    }
  }

  def createEvent() = Action { implicit request =>

    def eventCreator(event: Event) = {
      boxOfficeActor ! event
      Ok("Created")
    }

    eventForm.bindFromRequest.fold( err => BadRequest, eventCreator)
  }

  def buyTicket(eventNumber: Long) = Action {
    Ok(s"buy $eventNumber")
  }

}
