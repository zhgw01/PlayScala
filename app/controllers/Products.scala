package controllers

/**
 * Author: zhanggo
 * Date: 7/17/2014.
 */

import play.api.mvc.{Action, Controller, Flash}
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import play.api.i18n.Messages
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

  def newProduct = Action { implicit  request =>

    import request.flash
    val form = if (flash.get("error").isDefined)
      productForm.bind(flash.data)
    else
      productForm

    Ok(views.html.products.editProduct(form))
  }

  def save = Action { implicit request =>
      val newProductForm = productForm.bindFromRequest()

      newProductForm.fold(
        hasErrors = { form =>
          Redirect(routes.Products.newProduct()).
            flashing( Flash(form.data) + ("error" -> Messages("validation.errors"))
            )
        },

        success = { newProduct =>
          Product.add(newProduct)
          val message = Messages("products.new.success", newProduct.name)
          Redirect(routes.Products.show(newProduct.ean)).
            flashing("success" -> message)
        }
      )
  }




  private val productForm: Form[Product] = Form {
    mapping (
      "ean" -> longNumber.verifying(
          "validation.ean.duplicate", Product.findByEAN(_).isEmpty),
      "name" -> nonEmptyText,
      "description" -> nonEmptyText
    )(Product.apply)(Product.unapply)
  }
}
