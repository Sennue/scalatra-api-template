package com.sennue.api_template

import org.scalatra._
import scalate.ScalateSupport
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database

case class ApiServlet(db:Database) extends SennueApiTemplateStack with SlickRoutes

case class MessagePost(id: String, message: String)
case class MessageResult(success: Boolean, id: String, message: String)
case class ErrorResult(success: Boolean, error: String, message: String)

trait SlickRoutes extends SennueApiTemplateStack with JacksonJsonSupport {

  val db: Database

  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  error {
    case throwable: Throwable => InternalServerError(ErrorResult(false, throwable.getClass.getSimpleName, throwable.getMessage))
  }

  get("/") {
    MessageResult(true, "unknown-user", "Hello, world!")
  }

  get("/error/?") {
    throw new Exception("This endpoint always fails.")
  }

  post("/echo/?") {
    val messagePost = parsedBody.extract[MessagePost]
    MessageResult(true, messagePost.id, messagePost.message)
  }

}

