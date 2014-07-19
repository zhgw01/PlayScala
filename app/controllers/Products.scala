package controllers

/**
 * Author: zhanggo
 * Date: 7/17/2014.
 */

import play.api.mvc.{Action, Controller}
import models.Product

object Products extends Controller{

  def list = Action {
    implicit  request =>
      val products = Product.findAll
      Ok(views.html.products.list(products))
  }

  def show(ean: Long) = Action { implicit request =>
      Product.findByEAN(ean).map { product =>
        Ok(views.html.products.details(product))
      }.getOrElse(NotFound)
  }
}
