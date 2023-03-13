package com.github.apyanov.webtours;

import com.github.apyanov.webtours.scenarios.OrderTicketScenario;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class MaxPerformanceSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http.baseUrl("http://webtours.load-test.ru:1080")
            .inferHtmlResources(AllowList(), DenyList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg", ".*detectportal\\.firefox\\.com.*"))
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
            .acceptEncodingHeader("gzip, deflate")
            .acceptLanguageHeader("en-US,en;q=0.5")
            .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/110.0");

    {

        setUp(
                OrderTicketScenario.orderTickets.injectOpen(
                        incrementUsersPerSec(1)
                                .times(10)
                                .eachLevelLasting(Duration.of(1, ChronoUnit.MINUTES))
                                .separatedByRampsLasting(Duration.of(20, ChronoUnit.SECONDS))
                                .startingFrom(0)
                )
        ).maxDuration(Duration.of(10, ChronoUnit.MINUTES))
                .assertions(
                        global().responseTime().max().lt(1000),
                        global().successfulRequests().percent().gte(95.0))
                .protocols(httpProtocol);
    }
}