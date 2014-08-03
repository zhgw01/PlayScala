package controllers

/**
 * Created by galway on 7/30/14.
 */

import play.api.libs.ws._
import play.api.Play.current
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

object Proxy extends Controller{

  def linkedIn = Action.async{

      WS.url("http://engineering.linkedin.com/play/play-framework-async-io-without-thread-pool-and-callback-hell")
        .get().map {
        case res => Ok(res.body).as(res.allHeaders("Content-Type")(0))
      }
  }
}
