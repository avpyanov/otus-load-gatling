package com.github.apyanov.webtours.scenarios;

import com.github.apyanov.webtours.actions.Actions;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.scenario;

public class OrderTicketScenario {

    private static final Logger logger = LoggerFactory.getLogger(OrderTicketScenario.class);
    private static final FeederBuilder<String> users = csv("users.csv").eager().random();
    private static final FeederBuilder<String> payment = csv("payment.csv").eager().random();

    public static ScenarioBuilder orderTickets = scenario("Order tickets")
            .feed(users)
            .feed(payment)
            .exec(Actions.openWebTours)
            .exitHereIfFailed()
            .exec(Actions.login)
            .exitHereIfFailed()
            .exec(Actions.openFlights)
            .exec(
                    session -> {
                        Random random = new Random();
                        List<String> departList = session.getList("departList");
                        String departCity = departList.get(random.nextInt(departList.size()));
                        logger.warn("departCity {}", departCity);
                        List<String> arriveList = session.getList("arriveList");
                        logger.warn("arriveList {}", arriveList);
                        List<String> filtredList = arriveList.stream().filter(l -> !l.equals(departCity)).collect(Collectors.toList());
                        logger.warn("arriveList {}", filtredList);
                        String arriveCity = filtredList.get(random.nextInt(filtredList.size()));
                        logger.warn("arriveCity {}", arriveCity);
                        List<String> seatPrefList = session.getList("seatPrefList");
                        String seatPref = seatPrefList.get(random.nextInt(seatPrefList.size()));
                        logger.warn("seatPref {}", seatPref);
                        List<String> seatTypeList = session.getList("seatTypeList");
                        String seatType = seatTypeList.get(random.nextInt(seatPrefList.size()));
                        logger.warn("seatType {}", seatType);
                        Map<String, Object> map = new HashMap<>();
                        map.put("arriveCity", arriveCity);
                        map.put("departCity", departCity);
                        map.put("seatPref", seatPref);
                        map.put("seatType", seatType);
                        return session.setAll(map);
                    })
            .exec(Actions.findFlight)
            .exec(
                    session -> {
                        Random random = new Random();
                        List<String> outboundFlightList = session.getList("outboundFlightList");
                        String outboundFlight = outboundFlightList.get(random.nextInt(outboundFlightList.size()));
                        List<String> returnFlightList = session.getList("returnFlightList");
                        String returnFlight = returnFlightList.get(random.nextInt(returnFlightList.size()));
                        Map<String, Object> map = new HashMap<>();
                        map.put("outboundFlight", outboundFlight);
                        map.put("returnFlight", returnFlight);
                        logger.warn("returnFlight {}", returnFlight);
                        logger.warn("outboundFlight {}", outboundFlight);
                        return session.setAll(map);
                    })
            .exec(Actions.selectFlight)
            .exec(Actions.fillPayment);
}