package com.sennue.api_template

import org.scalatra.test.specs2._
import org.json4s.{DefaultFormats, Formats}
import org.json4s.jackson.JsonMethods._
import scala.reflect._
import com.mchange.v2.c3p0.ComboPooledDataSource
import com.sennue.api_template.ConfiguredPostgresDriver.simple._
import java.sql.Timestamp
import scala.slick.jdbc.JdbcBackend.Database
import com.sennue.api_template.models._
import org.slf4j.Logger
import org.slf4j.LoggerFactory

// For more on Specs2, see http://etorreborre.github.com/specs2/guide/org.specs2.guide.QuickStart.html
class ApiServletSpec extends ScalatraSpec {

  var logger: Logger = LoggerFactory.getLogger(classOf[ApiServletSpec])
  protected implicit val jsonFormats: Formats = DefaultFormats
  val db = Database.forURL(url = "jdbc:postgresql://localhost:5432/database-name?user=username", driver = "org.postgresql.Driver")

  addServlet(new ApiServlet(Database.forDataSource(new ComboPooledDataSource)), "/*")

  val API_USER_ID = "unit-test"

  val ECHO_USERNAME = "username"
  val ECHO_MESSAGE = "Message."
  val ECHO_REQUEST_GOOD = f"""{"userId":"$API_USER_ID%s","username":"$ECHO_USERNAME%s","message":"$ECHO_MESSAGE%s"}"""
  val ECHO_REQUEST_BAD = """{}"""

  def is =
    "GET / on ApiServlet"                        ^
    "should return status 200"                   ! getStatusCode("/", 200)^
    "GET /error on ApiServlet"                   ^
    "should return status 500"                   ! getStatusCode("/error/", 500)^
    "POST /echo on ApiServlet"                   ^
    "should return status 200"                   ! postStatusCode("/echo", ECHO_REQUEST_GOOD, 200)^
    "POST /echo on ApiServlet"                   ^
    "should have success status"                 ! postResponseBodySuccess("/echo", ECHO_REQUEST_GOOD, true)^
    "POST /echo on ApiServlet"                   ^
    "should return passed in user id"            ! postResponseBodyKeyEqualsValue("/echo", ECHO_REQUEST_GOOD, "userId", API_USER_ID)^
    "POST /echo on ApiServlet"                   ^
    "should return passed in username"           ! postResponseBodyKeyEqualsValue("/echo", ECHO_REQUEST_GOOD, "username", ECHO_USERNAME)^
    "POST /echo on ApiServlet"                   ^
    "should return passed in message"            ! postResponseBodyKeyEqualsValue("/echo", ECHO_REQUEST_GOOD, "message", ECHO_MESSAGE)^
    "POST /echo on ApiServlet"                   ^
    "bad JSON should return status 500"          ! postStatusCode("/echo", ECHO_REQUEST_BAD, 500)^
    "POST /echo on ApiServlet"                   ^
    "bad JSON should have failure status"        ! postResponseBodySuccess("/echo", ECHO_REQUEST_BAD, false)^
    "POST /echo on ApiServlet"                   ^
    "bad JSON should have error type"            ! postResponseBodyKeyOfType[String]("/echo", ECHO_REQUEST_BAD, "error")^
    "POST /echo on ApiServlet"                   ^
    "bad JSON should have error value"           ! postResponseBodyKeyOfType[String]("/echo", ECHO_REQUEST_BAD, "message")^
    "insert test"                                ^
    "new row should be created"                  ! {db withSession { implicit session => 1 must_== (messages += Message(-1, API_USER_ID, new Timestamp(System.currentTimeMillis()), true, ECHO_USERNAME, ECHO_MESSAGE)) } }^
                                                 end

  def getStatusCode(uri:String, code:Int) = 
    get(uri) {
      status must_== code
    }

  def postStatusCode(uri:String, json:String, code:Int) = 
    postJson(uri, json) {
      status must_== code
    }

  def postResponseBodySuccess(uri:String, json:String, success:Boolean) =
    postJson(uri, json) {
      (parse(response.body) \ "success").extract[Boolean] must_== success
    }

  def postResponseBodyKeyOfType[A](uri:String, json:String, key:String)(implicit m: Manifest[A]) = 
    postJson(uri, json) {
      (parse(response.body) \ "data" \ key).extract[A].getClass() must_== getRuntimeClass[A]
    }

  def postResponseBodyKeyEqualsValue[A](uri:String, json:String, key:String, value:A)(implicit m: Manifest[A]) = 
    postJson(uri, json) {
      // extract value for key and make sure it is the expected value
      (parse(response.body) \ "data" \ key).extract[A] must_== value
    }

  // Reference: https://groups.google.com/forum/#!topic/scalatra-user/I_vPKT-twRY
  def postJson[A](uri:String, json:String)(f: => A): A =
    post(uri, json.getBytes("utf-8"), Map("Content-Type" -> "application/json"))(f)

  // got help in #scala on irc.freenode.net for this one
  def getRuntimeClass[T: ClassTag]() = implicitly[ClassTag[T]].runtimeClass
}

