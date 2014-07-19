package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  implicit val flash = Flash.emptyCookie

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

}