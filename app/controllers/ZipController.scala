package controllers

import play.api.mvc.{Action, Controller}


object ZipController extends Controller{

  def zip = Action {
    Ok("zip")
  }
}
