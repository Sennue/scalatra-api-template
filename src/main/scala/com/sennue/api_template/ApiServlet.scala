package com.sennue.api_template

import org.scalatra._
import scalate.ScalateSupport
import org.json4s.{DefaultFormats, Formats}
import org.json4s.MappingException
import org.scalatra.json._

class ApiServlet extends SennueApiTemplateStack with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  get("/") {
    MessageResult(true, "???", "Hello, world!")
  }

  post("/echo/?") {
    try {
      val messagePost = parsedBody.extract[MessagePost]
      MessageResult(true, messagePost.id, messagePost.message)
    }
    catch {
      case mappingException: MappingException => ErrorResult(false, mappingException.msg)
    }
  }

}

case class MessagePost(id: String, message: String)
case class MessageResult(success: Boolean, id: String, message: String)
case class ErrorResult(success: Boolean, error: String)

