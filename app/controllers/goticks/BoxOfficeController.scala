package controllers.goticks

import akka.actor.Props
import akka.goticks.BoxOffice
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.libs.concurrent.Akka
import play.api.mvc.{Action, Controller}

object BoxOfficeController extends Controller{
  import akka.goticks.TicketSeller._

  val boxOfficeActor = Akka.system.actorOf(Props[BoxOffice], "BoxOffice")

  val eventForm = Form(mapping(
    "event" -> of[String],
    "nrOfTickets" -> of[Int]
  )(Event.apply)(Event.unapply))


  def events = Action {
    Ok("Get Events")
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
