package controllers.com.learn.controller

import java.io.File

import play.api.{Play, Logger}
import play.api.mvc.{Action, Controller}
import play.api.Play.current

import scala.io.Source
import scala.util.Random

object Session extends Controller{

  val log = Logger(this.getClass())

  def index = Action { implicit  request =>
    //val url = controllers.routes.Assets.at("/htmls/session.html").url
    //TemporaryRedirect(url)

    request.session.get("jsFile").map {
      filename =>
        val jsContent = Source.fromFile(filename).mkString
        log.info(s"Set Javascript: $jsContent")
        Ok(views.html.sessions.session(jsContent))
    }.getOrElse {
      val operand_x = Random.nextInt(100)
      val operand_y = Random.nextInt(100)

      val operators = Array('+', '-', '*', '/')
      val operator = operators(Random.nextInt(operators.length))

      log.info(s"$operand_x $operator $operand_y")

      Ok(views.html.sessions.session("")).addingToSession(
        "operand_x" -> s"$operand_x",
        "operand_y" -> s"$operand_y",
        "operator" -> s"$operator"
      )
    }
  }

  def upload = Action(parse.multipartFormData) {implicit request =>

    log.info("start to uplaod file")
    request.body.file("jsFile").map { jsFile =>
      import java.io.File
      val filename = jsFile.filename
      val relativePath = "public/javascripts/" + filename
      val destFile = Play.application.getFile(relativePath)
      val path = destFile.getAbsolutePath
      log.info(s"upload to $path")
      jsFile.ref.moveTo(destFile)
      Redirect(routes.Session.index).addingToSession(
        "jsFile" -> path
      )
    }.getOrElse {
      BadRequest("Failed to get the file")
    }
  }
}
