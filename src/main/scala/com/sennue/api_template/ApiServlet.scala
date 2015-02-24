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

  get("/") {
    MessageResult(true, "Hello, world!")
  }

}

case class MessageResult(success: Boolean, message: String)

