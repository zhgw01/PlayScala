package controllers.com.learn.controller

import play.api.mvc.{Action, Controller}

object Session extends Controller{

  def index = Action {
    val url = controllers.routes.Assets.at("htmls/session.html").url
    TemporaryRedirect(url)
  }
}
