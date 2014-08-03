package controllers

/**
 * Created by galway on 7/30/14.
 */

import play.api.libs.json.Json
import play.api.libs.ws._
import play.api.Play.current
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Proxy extends Controller{

  def linkedIn = Action.async{

      WS.url("http://engineering.linkedin.com/play/play-framework-async-io-without-thread-pool-and-callback-hell")
        .get().map {
        case res => Ok(res.body).as(res.allHeaders("Content-Type")(0))
      }
  }

  def latency = Action.async {
    val start = System.currentTimeMillis();

    def getLatency(r: Any): Long = System.currentTimeMillis() - start

    val yahooTime = WS.url("http://www.yahoo.com").get().map(getLatency)
    val bingTime = WS.url("http://www.bing.com").get().map(getLatency)

    Future.sequence(Seq(yahooTime, bingTime)).map {
      case times => Ok(Json.toJson(
        Map("yahoo" -> times(0), "bing" -> times(1))
      ))
    }


  }
}
