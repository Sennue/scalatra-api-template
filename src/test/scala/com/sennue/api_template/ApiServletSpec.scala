package com.sennue.api_template

import org.scalatra.test.specs2._
import org.json4s.{DefaultFormats, Formats}
import org.json4s.jackson.JsonMethods._
import scala.reflect._

// For more on Specs2, see http://etorreborre.github.com/specs2/guide/org.specs2.guide.QuickStart.html
class ApiServletSpec extends ScalatraSpec {

  protected implicit val jsonFormats: Formats = DefaultFormats

  addServlet(classOf[ApiServlet], "/*")

  val ECHO_REQUEST_GOOD = """{"id":"unit-test","message":"Message."}"""
  val ECHO_RESPONSE_GOOD = """{"success":true,"id":"unit-test","message":"Message."}"""
  val ECHO_REQUEST_BAD = """{}"""
  val ECHO_RESPONSE_BAD = """{"success":false,"error":"MappingException","message":"No usable value for id\nDid not find value which can be converted into java.lang.String"}"""

  def is =
    "GET / on ApiServlet"                        ^
    "should return status 200"                   ! getStatusCode("/",200)^
    "GET /error on ApiServlet"                   ^
    "should return status 500"                   ! getStatusCode("/error/",500)^
    "POST /echo on ApiServlet"                   ^
    "should return status 200"                   ! postStatusCode("/echo",ECHO_REQUEST_GOOD,200)^
    "POST /echo on ApiServlet"                   ^
    "should return message json"                 ! postResponseBody("/echo",ECHO_REQUEST_GOOD,ECHO_RESPONSE_GOOD)^
    "POST /echo on ApiServlet"                   ^
    "bad JSON should return status 500"          ! postStatusCode("/echo",ECHO_REQUEST_BAD,500)^
    "POST /echo on ApiServlet"                   ^
    "bad JSON should return error message json"  ! postResponseBody("/echo",ECHO_REQUEST_BAD,ECHO_RESPONSE_BAD)^
    "POST /echo on ApiServlet"                   ^
    "bad JSON should return error message json"  ! postResponseBodyKeyValue("/echo",ECHO_REQUEST_BAD,"success",false)^
    "POST /echo on ApiServlet"                   ^
    "bad JSON should return error message json"  ! postResponseBodyKeyValue("/echo",ECHO_REQUEST_BAD,"error","MappingException")^
    "POST /echo on ApiServlet"                   ^
    "bad JSON should return error message json"  ! postResponseBodyKeyOfType[String]("/echo",ECHO_REQUEST_BAD,"message")^
                                              end

  def getStatusCode(uri:String, code:Int) = 
    get(uri) {
      status must_== code
    }

  def postStatusCode(uri:String, json:String, code:Int) = 
    postJson(uri, json) {
      status must_== code
    }

  def postResponseBody(uri:String, json:String, body:String) = 
    postJson(uri, json) {
      response.body must_== body
    }

  def postResponseBodyKeyOfType[A](uri:String, json:String, key:String)(implicit m: Manifest[A]) = 
    postJson(uri, json) {
      (parse(response.body) \ key).extract[A].getClass() must_== getRuntimeClass[A]
    }

  def postResponseBodyKeyValue[A](uri:String, json:String, key:String, value:A)(implicit m: Manifest[A]) = 
    postJson(uri, json) {
      // extract value for key and make sure it is the expected value
      (parse(response.body) \ key).extract[A] must_== value
    }

  // Reference: https://groups.google.com/forum/#!topic/scalatra-user/I_vPKT-twRY
  def postJson[A](uri:String, json:String)(f: => A): A =
    post(uri, json.getBytes("utf-8"), Map("Content-Type" -> "application/json"))(f)

  // Reference: https://groups.google.com/forum/#!topic/scalatra-user/I_vPKT-twRY
  def postJson[A](uri:String, json:String, headers:Map[String, String])(f: => A): A =
    post(uri, json.getBytes("utf-8"), Map("Content-Type" -> "application/json") ++ headers)(f)

  // got help in #scala on irc.freenode.net for this one
  def getRuntimeClass[T: ClassTag]() = implicitly[ClassTag[T]].runtimeClass
}
