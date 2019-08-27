package test.load.oauth

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._

import java.util.concurrent.ThreadLocalRandom

import scala.concurrent.duration._

class TkeyPasswordGrantType extends Simulation {

  val totalConcurrency = Integer.getInteger("totalConcurrency", 1000).toInt
  val repeatTime = Integer.getInteger("repeatTime", 10).toInt
  val injectTime = Integer.getInteger("injectTime", 10).toInt

  val baseUrl = "http://sso.cdk8s.com:9091/sso"
  val testPath = "/oauth/token"

  val username = "admin"
  val password = "123456"
  val clientId = "test_client_id_1"
  val clientSecret = "test_client_secret_1"

  val headersJson = Map("Content-Type" -> "application/x-www-form-urlencoded")

  val httpProtocol = http
    .baseUrl(baseUrl)
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("zh-CN,zh;q=0.9")
    .acceptEncodingHeader("gzip, deflate, br")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.80 Safari/537.36")


  val oauthRequest = repeat(repeatTime) {
    exec(
      http("oauth_request")
        .post(testPath)
        .headers(headersJson)
        .formParam("grant_type", "password")
        .formParam("client_id", clientId)
        .formParam("client_secret", clientSecret)
        .formParam("username", username)
        .formParam("password", password)
    ).pause(10 millisecond)
  }

  val oauthLoad = scenario("oauth_load").exec(oauthRequest)

  setUp(
    oauthLoad.inject(rampUsers(totalConcurrency) during (injectTime))
  ).protocols(httpProtocol)


}
