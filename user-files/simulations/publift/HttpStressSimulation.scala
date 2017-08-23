package publift

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class HttpStressSimulation extends Simulation {

  val httpConf = http
    .baseURL("http://publift-sds-2017.appspot.com") // 5
    .acceptHeader("application/octet-stream,text/plain,text/javascript")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Unknown request by: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/60.0.3112.78 Chrome/60.0.3112.78 Safari/537.36")

  val scn = scenario("HttpStressSimulation")
    .exec(http("Page Load").get("/page_load"))
    .exec(http("Bid Requests").get("/bid_requests"))
    .exec(http("Bid results").get("/bid_results"))
    .exec(http("Creative render").get("/creative_render"))

  setUp(scn.inject(rampUsers(15000) over (10 seconds))).protocols(httpConf)
}