package com.sennue.api_template

import org.scalatra._
import scalate.ScalateSupport
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json._

class ApiServlet extends SennueApiTemplateStack with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  error {
    case throwable: Throwable => InternalServerError(ErrorResult(false, throwable.getClass.getSimpleName, throwable.getMessage))
  }

  get("/") {
    MessageResult(true, "???", "Hello, world!")
  }

  get("/error/?") {
    throw new Exception("This endpoint always fails.")
  }

  post("/echo/?") {
    val messagePost = parsedBody.extract[MessagePost]
    MessageResult(true, messagePost.id, messagePost.message)
  }

}

case class MessagePost(id: String, message: String)
case class MessageResult(success: Boolean, id: String, message: String)
case class ErrorResult(success: Boolean, error: String, message: String)

