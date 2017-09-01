package publift

import java.util.UUID
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.feeder._
import scala.concurrent.duration._

class WebSocketStressSimulation extends Simulation {

  val httpConf = http
//    .baseURL("http://publift-sds-2017.appspot.com")
    .baseURL("http://localhost:3000")
    .acceptHeader("application/octet-stream,text/plain,text/javascript")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Unknown request by: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/60.0.3112.78 Chrome/60.0.3112.78 Safari/537.36")
//    .wsBaseURL("ws://publift-sds-2017.appspot.com:65080")
    .wsBaseURL("ws://localhost:65080")

  val feeder = Iterator.continually(Map("user_id" -> UUID.randomUUID.toString))

  val scn = scenario("WebSocket")
    .feed(feeder)
    .exec(ws("Connect").open("/"))
    .exec(ws("Page Load")
      .sendText("""{"type": "page_load", "value": {"event_type": "page_load", "user_id": "${user_id}", "method": "ws"}}""")
    )
    .exec(ws("Bid Requests")
      .sendText("""{"type": "bid_requests", "value": {"event_type": "bid_requests", "user_id": "${user_id}", "method": "ws"}}""")
    )
    .exec(ws("Bid Results")
      .sendText("""{"type": "bid_results", "value": {"event_type": "bid_results", "user_id": "${user_id}", "method": "ws"}}""")
    )
    .exec(ws("Creative Render")
      .sendText("""{"type": "creative_render", "value": {"event_type": "creative_render", "user_id": "${user_id}", "method": "ws"}}""")
    )

  setUp(scn.inject(rampUsers(20000) over (20 seconds))).protocols(httpConf)
}
