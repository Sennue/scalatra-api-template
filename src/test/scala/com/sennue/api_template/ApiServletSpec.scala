package com.sennue.api_template

import org.scalatra.test.specs2._

// For more on Specs2, see http://etorreborre.github.com/specs2/guide/org.specs2.guide.QuickStart.html
class ApiServletSpec extends ScalatraSpec {

  addServlet(classOf[ApiServlet], "/*")

  val ECHO_REQUEST_GOOD = """{"id":"unit-test","message":"Message."}"""
  val ECHO_RESPONSE_GOOD = """{"id":"unit-test","message":"Echo this message!","success":true}"""
  val ECHO_REQUEST_BAD = """{}"""
  val ECHO_RESPONSE_BAD = """{"error":"MappingException","message":"No usable value for id\nDid not find value which can be converted into java.lang.String","success": false}"""

  def is =
    "GET / on ApiServlet"                        ^
    "should return status 200"                   ! getStatusCode("/",200)^
    "GET /error on ApiServlet"                   ^
    "should return status 500"                   ! getStatusCode("/error/",500)^
    "POST /echo on ApiServlet"                   ^
    "should return status 200"                   ! postStatusCode("/",ECHO_REQUEST_GOOD,200)^
    "POST /echo on ApiServlet"                   ^
    "should return message json"                 ! postResponseBody("/",ECHO_REQUEST_GOOD,ECHO_RESPONSE_GOOD)^
    "POST /echo on ApiServlet"                   ^
    "bad JSON should return status 500"          ! postStatusCode("/",ECHO_REQUEST_BAD,500)^
    "POST /echo on ApiServlet"                   ^
    "bad JSON should return error message json"  ! postResponseBody("/",ECHO_REQUEST_BAD,ECHO_RESPONSE_BAD)^
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

  // Reference: https://groups.google.com/forum/#!topic/scalatra-user/I_vPKT-twRY
  def postJson[A](uri:String, body:String)(f: => A): A =
    post(uri, body.getBytes("utf-8"), Map("Content-Type" -> "application/json"))(f)

  // Reference: https://groups.google.com/forum/#!topic/scalatra-user/I_vPKT-twRY
  def postJson[A](uri:String, body:String, headers:Map[String, String])(f: => A): A =
    post(uri, body.getBytes("utf-8"), Map("Content-Type" -> "application/json") ++ headers)(f)
}
