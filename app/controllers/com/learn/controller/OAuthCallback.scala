package controllers.com.learn.controller

import java.util.UUID

import play.api.{Logger, Application, Play}
import play.api.http.{MimeTypes, HeaderNames}
import play.api.libs.ws.WS
import play.api.mvc.{Results, Controller, Action}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object OAuthCallback extends Controller{
  val log = Logger(this.getClass())
  lazy val oauth2 = new OAuthCallback(Play.current)

  def callback(codeOpt: Option[String] = None, stateOpt: Option[String] = None) = Action.async {
    implicit request =>
      (for {
        code <- codeOpt
        state <- stateOpt
        oauthState <- request.session.get("oauth-state")
      } yield {
        if (state == oauthState) {
          log.info("Try to get access token")
//          oauth2.getToken(code).map { accessToken =>
//            Redirect(controllers.com.learn.controller.routes.OAuthCallback.success()).withSession("oauth-token" -> accessToken)
//          }.recover {
//            case ex: IllegalStateException => Unauthorized(ex.getMessage)
//          }

          val grantUrl = "http://localhost:9000/oauthcallback"
          val tokenResponse = WS.url("https://accounts.google.com/o/oauth2/token")(Play.current).
            withHeaders(
              HeaderNames.ACCEPT -> MimeTypes.JSON,
              HeaderNames.CONTENT_TYPE -> "application/x-www-form-urlencoded"
            ).
            post(
              Map(
                "client_id" -> Seq(oauth2.clientId),
                "client_secret"-> Seq(oauth2.clientSecret),
                "code" -> Seq(code),
                "grant_type" -> Seq("authorization_code"),
                "redirect_uri" -> Seq(grantUrl)
              ))

          tokenResponse.map {
            response =>
              log.info(response.json.toString)
              (response.json \ "access_token").asOpt[String].
                map{ accessToken => Ok(accessToken)}.
                getOrElse(BadRequest("Can't get access token from google response"))
          }
        }
        else {
          Future.successful(BadRequest("Invalid google login"))
        }
      }).getOrElse(Future.successful(BadRequest("No Parameters supplied")))
  }

  def grant = Action.async{ request =>
    log.info("grant")
    Future.successful(Ok("grant"))
  }

  def success = Action.async { request =>
    implicit val app = Play.current
    request.session.get("oauth-token").map { authToken =>
      WS.url("https://www.googleapis.com/calendar/v3/users/me/calendarList").
      withHeaders(HeaderNames.AUTHORIZATION -> s"token $authToken").
        get().map { response => Ok(response.json) }
    }.getOrElse {
      Future.successful(Unauthorized("No way Joe"))
    }
  }

  def index = Action { implicit request =>
    val callbackUrl = controllers.com.learn.controller.routes.OAuthCallback.callback(None, None).absoluteURL()
    val scope = "https://www.googleapis.com/auth/calendar"
    val state = UUID.randomUUID().toString
    val redirectUrl = oauth2.getAuthorizationUrl(scope, state, callbackUrl)

    Ok(views.html.oauth(redirectUrl)).withSession("oauth-state" -> state)
  }
}


class OAuthCallback(application: Application) {
    lazy val clientId = application.configuration.getString("google.client.id").get
    lazy val clientSecret = application.configuration.getString("google.client.secret").get
    val log = Logger(this.getClass)

    def getAuthorizationUrl(scope: String, state: String, redirectUrl: String): String = {
      val baseUrl = application.configuration.getString("google.redirect.url").get
      baseUrl.format(scope, state, redirectUrl, clientId);
    }

    def getToken(code: String): Future[String] = {
      //val grantUrl = controllers.com.learn.controller.routes.OAuthCallback.grant().absoluteURL().toString
      val grantUrl = "http://localhost:9000/oauthcallback"
//      val tokenResponse = WS.url("https://accounts.google.com/o/oauth2/token")(application).
//        withQueryString(
//        "client_id" -> clientId,
//        "client_secret"-> clientSecret,
//        "code" -> code,
//        "grant_type" -> "authorization_code",
//        "redirect_uri" -> grantUrl
//        ).
//        withHeaders(HeaderNames.ACCEPT -> MimeTypes.JSON).
//        post(Results.EmptyContent())

      val tokenResponse = WS.url("https://accounts.google.com/o/oauth2/token")(application).
        withHeaders(
          HeaderNames.ACCEPT -> MimeTypes.JSON,
          HeaderNames.CONTENT_TYPE -> "application/x-www-form-urlencoded"
        ).
        post(
          Map(
          "client_id" -> Seq(clientId),
          "client_secret"-> Seq(clientSecret),
          "code" -> Seq(code),
          "grant_type" -> Seq("authorization_code"),
          "redirect_uri" -> Seq(grantUrl)
          ))

      tokenResponse.flatMap { response =>
        log.info(response.json.toString)
        (response.json \ "access_token").asOpt[String].
          map{ accessToken => Future.successful(accessToken)}.
          getOrElse(Future.failed[String](new IllegalStateException("sod off")))
      }
    }
}