package com.sennue.api_template

import org.scalatra._
import scalate.ScalateSupport
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import scala.slick.jdbc.JdbcBackend.Database
import com.sennue.api_template.ConfiguredPostgresDriver.simple._
import com.sennue.api_template.models._

case class ApiServlet(db:Database) extends SennueApiTemplateStack with SlickRoutes

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

  get("/message/?") {
  }

  post("/message/?") {
  }
}

