package publift

import java.util.UUID

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class HttpStressSimulation extends Simulation {

  val httpConf = http
//    .baseURL("http://publift-sds-2017.appspot.com")
    .baseURL("http://localhost:3000")
    .acceptHeader("application/octet-stream,text/plain,text/javascript")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Unknown request by: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/60.0.3112.78 Chrome/60.0.3112.78 Safari/537.36")

  val randomUser = UUID.randomUUID.toString

  val scn = scenario("HttpStressSimulation")
    .exec(
      http("Page Load")
        .post("/page_load")
        .body(StringBody(s"""{"type": "page_load", "value": {"event_type": "page_load", "user_id": "${randomUser}", "method": "http"}}"""))
        .asJSON
    )
    .exec(
      http("Bid Requests")
        .post("/bid_requests")
        .body(StringBody(s"""{"type": "bid_requests", "value": {"event_type": "bid_requests", "user_id": "${randomUser}", "method": "http"}}"""))
        .asJSON
    )
    .exec(
      http("Bid results")
        .post("/bid_results")
        .body(StringBody(s"""{"type": "bid_results", "value": {"event_type": "bid_results", "user_id": "${randomUser}", "method": "http"}}"""))
        .asJSON
    )
    .exec(
      http("Creative render")
        .post("/creative_render")
        .body(StringBody(s"""{"type": "creative_render", "value": {"event_type": "creative_render", "user_id": "${randomUser}", "method": "http"}}"""))
        .asJSON
    )

  setUp(scn.inject(rampUsers(10000) over (10 seconds))).protocols(httpConf)
}
