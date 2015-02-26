package com.sennue.api_template

import org.scalatra._
import scalate.ScalateSupport
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._
import scala.slick.jdbc.JdbcBackend.Database
import com.sennue.api_template.ConfiguredPostgresDriver.simple._
import com.sennue.api_template.models._
import com.sennue.api_template.models.MessageTable
import java.sql.Timestamp

case class ApiServlet(db:Database) extends SennueApiTemplateStack with SlickRoutes

trait SlickRoutes extends SennueApiTemplateStack with JacksonJsonSupport {

  val db: Database

  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  error {
    case throwable: Throwable => InternalServerError(Result(false, Error(throwable.getClass.getSimpleName, throwable.getMessage)))
  }

  get("/") {
    Result[Message](true, new Message(0, "system", new Timestamp(System.currentTimeMillis()), true, "anonymous", "Hello, world!"))
  }

  get("/error/?") {
    throw new Exception("This endpoint always fails.")
  }

  post("/echo/?") {
    val message = parsedBody.extract[Message]
    Result(true, message)
  }

  get("/message/?") {
    db withDynSession { implicit session: Session =>
      val l = messages.drop(0).take(5)
    }
  }

  post("/message/?") {
    db withDynSession { implicit session: Session =>
      messages.insert(new Message(-1, "userId", new Timestamp(System.currentTimeMillis()), true, "username", "message"))
    }
  }
}

