package publift

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class WebSocketStressSimulation extends Simulation {

  val httpConf = http
    .baseURL("http://publift-sds-2017.appspot.com")
    .acceptHeader("application/octet-stream,text/plain,text/javascript")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Gatling2")
    .wsBaseURL("ws://publift-sds-2017.appspot.com:3000")

  val scn = scenario("WebSocket")
    .exec(ws("Connect").open("/"))
    .exec(ws("Page Load").sendText(

          """{
        |"type": "page_load",
        ||"}""".stripMargin))
    .exec(ws("Bid Requests").sendText("""{"type": "bid_requests"}"""))
    .exec(ws("Bid Results").sendText("""{"type": "bid_results"}"""))
    .exec(ws("Creative Render").sendText("""{"type": "creative_render"}"""))

  setUp(scn.inject(rampUsers(5000) over (10 seconds))).protocols(httpConf)
}