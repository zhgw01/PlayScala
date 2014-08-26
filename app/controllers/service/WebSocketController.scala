package controllers.service

import java.lang.management.ManagementFactory

import scala.concurrent.duration.DurationInt

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Promise
import play.api.libs.iteratee.{Enumerator, Iteratee}
import play.api.mvc.{Action, WebSocket, Controller}

object WebSocketController extends Controller{

  def statusPage = Action {
    implicit request =>
      Ok(views.html.websocket.websocket(request))
  }

  def statusFeed = WebSocket.using[String] {
    implicit request =>

      def getLoadAverage = {
        "%1.2f" format ManagementFactory.getOperatingSystemMXBean.getSystemLoadAverage
      }

      val in = Iteratee.ignore[String]
      val out = Enumerator.repeatM {
        Promise.timeout(getLoadAverage, 3 seconds)
      }

      (in, out)
  }

}
