package com.sennue.api_template

import org.scalatra.test.specs2._
import org.json4s.{DefaultFormats, Formats}
import org.json4s.jackson.JsonMethods._
import scala.reflect._
import com.mchange.v2.c3p0.ComboPooledDataSource
import scala.slick.jdbc.JdbcBackend.Database
import com.sennue.api_template.models._

// For more on Specs2, see http://etorreborre.github.com/specs2/guide/org.specs2.guide.QuickStart.html
class ApiServletSpec extends ScalatraSpec {

  protected implicit val jsonFormats: Formats = DefaultFormats

  addServlet(new ApiServlet(Database.forDataSource(new ComboPooledDataSource)), "/*")

  val API_USER = "unit-test"

  val ECHO_MESSAGE = "Message."
  val ECHO_REQUEST_GOOD = f"""{"id":"$API_USER%s","message":"$ECHO_MESSAGE%s"}"""
  val ECHO_REQUEST_BAD = """{}"""

  def is =
    "GET / on ApiServlet"                        ^
    "should return status 200"                   ! getStatusCode("/", 200)^
    "GET /error on ApiServlet"                   ^
    "should return status 500"                   ! getStatusCode("/error/", 500)^
    "POST /echo on ApiServlet"                   ^
    "should return status 200"                   ! postStatusCode("/echo", ECHO_REQUEST_GOOD, 200)^
    "POST /echo on ApiServlet"                   ^
    "should have success status"                 ! postResponseBodyKeyEqualsValue("/echo", ECHO_REQUEST_GOOD, "success", true)^
    "POST /echo on ApiServlet"                   ^
    "should return passed in user id"            ! postResponseBodyKeyEqualsValue("/echo", ECHO_REQUEST_GOOD, "id", API_USER)^
    "POST /echo on ApiServlet"                   ^
    "should return passed in message"            ! postResponseBodyKeyEqualsValue("/echo", ECHO_REQUEST_GOOD, "message", ECHO_MESSAGE)^
    "POST /echo on ApiServlet"                   ^
    "bad JSON should return status 500"          ! postStatusCode("/echo", ECHO_REQUEST_BAD, 500)^
    "POST /echo on ApiServlet"                   ^
    "bad JSON should have failure status"        ! postResponseBodyKeyEqualsValue("/echo", ECHO_REQUEST_BAD, "success", false)^
    "POST /echo on ApiServlet"                   ^
    "bad JSON should have error type"            ! postResponseBodyKeyOfType[String]("/echo", ECHO_REQUEST_BAD, "error")^
    "POST /echo on ApiServlet"                   ^
    "bad JSON should have error value"           ! postResponseBodyKeyOfType[String]("/echo", ECHO_REQUEST_BAD, "message")^
                                                 end

  def getStatusCode(uri:String, code:Int) = 
    get(uri) {
      status must_== code
    }

  def postStatusCode(uri:String, json:String, code:Int) = 
    postJson(uri, json) {
      status must_== code
    }

  def postResponseBodyKeyOfType[A](uri:String, json:String, key:String)(implicit m: Manifest[A]) = 
    postJson(uri, json) {
      (parse(response.body) \ key).extract[A].getClass() must_== getRuntimeClass[A]
    }

  def postResponseBodyKeyEqualsValue[A](uri:String, json:String, key:String, value:A)(implicit m: Manifest[A]) = 
    postJson(uri, json) {
      // extract value for key and make sure it is the expected value
      (parse(response.body) \ key).extract[A] must_== value
    }

  // Reference: https://groups.google.com/forum/#!topic/scalatra-user/I_vPKT-twRY
  def postJson[A](uri:String, json:String)(f: => A): A =
    post(uri, json.getBytes("utf-8"), Map("Content-Type" -> "application/json"))(f)

  // got help in #scala on irc.freenode.net for this one
  def getRuntimeClass[T: ClassTag]() = implicitly[ClassTag[T]].runtimeClass
}

