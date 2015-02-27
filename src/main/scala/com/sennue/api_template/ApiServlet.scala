package com.sennue.api_template

import org.scalatra._
import scalate.ScalateSupport
import org.json4s.{DefaultFormats, Formats}
import org.slf4j.{Logger, LoggerFactory}
import org.scalatra.json._
import scala.slick.jdbc.JdbcBackend.Database
import com.sennue.api_template.ConfiguredPostgresDriver.simple._
import com.sennue.api_template.models._
import java.sql.Timestamp

case class ApiServlet(db:Database) extends SennueApiTemplateStack with SlickRoutes

trait SlickRoutes extends SennueApiTemplateStack with JacksonJsonSupport {

  val db: Database
  val logger =  LoggerFactory.getLogger(getClass)

  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  error {
    case throwable: Throwable => InternalServerError(Result(false, Error(throwable.getClass.getSimpleName, throwable.getMessage)))
  }

  get("/") {
    logger.info("hello world logger")
    Result[Message](true, new Message(0, "system", new Timestamp(System.currentTimeMillis()), true, "anonymous", "Hello, world!"))
  }

  get("/error/?") {
    throw new Exception("This endpoint always fails.")
  }

  post("/echo/?") {
    val message = parsedBody.extract[MessagePost]
    Result(true, message)
  }

  get("/message/?") {
    var data: List[Message] = Nil
    db withDynSession { implicit session: Session =>
      data = messages.drop(0).take(5).list
    }
    Result[List[Message]](true, data)
  }

  post("/message/?") {
    var result: Int = 0
    var message = parsedBody.extract[MessagePost]
    db withDynSession { implicit session: Session =>
      //result = messages.insert(Message(-1, message.userId, new Timestamp(System.currentTimeMillis()), true, message.username, message.message))
      result = messages += Message(-1, message.userId, new Timestamp(System.currentTimeMillis()), true, message.username, message.message)
    }
    Result(true, result)
  }
}

