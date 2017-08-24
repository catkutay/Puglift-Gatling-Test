package publift

import java.util.UUID
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class WebSocketStressSimulation extends Simulation {

  val httpConf = http
    .baseURL("http://localhost:3000")
    .acceptHeader("application/octet-stream,text/plain,text/javascript")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Gatling2")
    .wsBaseURL("ws://localhost:3000")

  val randomUser = UUID.randomUUID.toString

  val scn = scenario("WebSocket")
    .exec(ws("Connect").open("/"))
    .exec(ws("Page Load").sendText(s"""{"type": "page_load", "value": {"event_type": "page_load", "user_id": "${randomUser}"}}"""))
    .exec(ws("Bid Requests").sendText(s"""{"type": "bid_requests", "value": {"event_type": "bid_requests", "user_id": "${randomUser}"}}"""))
    .exec(ws("Bid Results").sendText(s"""{"type": "bid_results", "value": {"event_type": "bid_results", "user_id": "${randomUser}"}}"""))
    .exec(ws("Creative Render").sendText(s"""{"type": "creative_render", "value": {"event_type": "creative_render", "user_id": "${randomUser}"}}"""))

  setUp(scn.inject(rampUsers(10000) over (10 seconds))).protocols(httpConf)
}