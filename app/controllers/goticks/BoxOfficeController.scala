package controllers.goticks

import play.api.mvc.{Action, Controller}

object BoxOfficeController extends Controller{

  def events = Action {
    Ok("Get Events")
  }

  def createEvent() = Action {
    Ok("Created")
  }

  def buyTicket(eventNumber: Long) = Action {
    Ok(s"buy $eventNumber")
  }

}s
