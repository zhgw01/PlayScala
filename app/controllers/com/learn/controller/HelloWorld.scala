package controllers.com.learn.controller

import play.api.mvc.{Action, Controller}

/**
 * Author: zhanggo
 * Date: 7/14/2014.
 */
object HelloWorld extends Controller
{
  def index = Action {
    Ok("Hello World")
  }

  def index(name: String) = Action {
    Ok(views.html.hello(name))
  }
}
