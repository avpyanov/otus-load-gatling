package com.github.apyanov.webtours.actions;


import io.gatling.javaapi.http.HttpRequestActionBuilder;

import static io.gatling.javaapi.core.CoreDsl.css;
import static io.gatling.javaapi.http.HttpDsl.http;

public class Actions {

    public static HttpRequestActionBuilder openWebTours = http("Open 'Webtours' page")
            .get("/cgi-bin/welcome.pl?signOff=true")
            .resources(
                    http("request_1")
                            .get("/cgi-bin/nav.pl?in=home")
                            .check(css("[name=userSession]", "value").saveAs("userSession")));


    public static HttpRequestActionBuilder login = http("Login")
            .post("/cgi-bin/login.pl")
            .formParam("userSession", "#{userSession}")
            .formParam("username", "#{user}")
            .formParam("password", "#{password}")
            .formParam("login.x", "47")
            .formParam("login.y", "8")
            .formParam("JSFormSubmit", "off")
            .resources(
                    http("request_4")
                            .get("/cgi-bin/login.pl?intro=true")
                                    .check(css("blockquote>b").is(session -> session.get("user"))),
                    http("request_5")
                            .get("/cgi-bin/nav.pl?page=menu&in=home"));

    public static HttpRequestActionBuilder openFlights = http("Open 'Flights' page")
            .get("/cgi-bin/welcome.pl?page=search")
            .resources(
                    http("nav.pl?page=menu&in=flights")
                            .get("/cgi-bin/nav.pl?page=menu&in=flights"),
                    http("reservations.pl")
                            .get("/cgi-bin/reservations.pl?page=welcome")
                            .check(css("[name='depart']>option", "value").findAll().saveAs("departList"))
                            .check(css("[name='arrive']>option", "value").findAll().saveAs("arriveList"))
                            .check(css("[name='seatPref']", "value").findAll().saveAs("seatPrefList"))
                            .check(css("[name='seatType']", "value").findAll().saveAs("seatTypeList"))
                            .check(css("[name='departDate']", "value").saveAs("departDate"))
                            .check(css("[name='returnDate']", "value").saveAs("returnDate")));

    public static HttpRequestActionBuilder findFlight = http("Find flight")
            .post("/cgi-bin/reservations.pl")
            .formParam("advanceDiscount", "0")
            .formParam("depart", session -> session.getString("departCity"))
            .formParam("departDate", session -> session.getString("departDate"))
            .formParam("arrive", session -> session.getString("arriveCity"))
            .formParam("returnDate", session -> session.getString("returnDate"))
            .formParam("numPassengers", "1")
            .formParam("roundtrip", "on")
            .formParam("seatPref", session -> session.getString("seatPref"))
            .formParam("seatType", session -> session.getString("seatType"))
            .formParam("findFlights.x", "56").formParam("findFlights.y", "9")
            .formParam(".cgifields", "roundtrip")
            .formParam(".cgifields", "seatType")
            .formParam(".cgifields", "seatPref")
            .check(css("[name='outboundFlight']", "value").findAll().saveAs("outboundFlightList"))
            .check(css("[name='returnFlight']", "value").findAll().saveAs("returnFlightList"));

    public static HttpRequestActionBuilder selectFlight = http("Select flight")
            .post("/cgi-bin/reservations.pl")
            .formParam("outboundFlight", session -> session.getString("outboundFlight"))
            .formParam("returnFlight", session -> session.getString("returnFlight"))
            .formParam("numPassengers", "1")
            .formParam("advanceDiscount", "0")
            .formParam("seatType", session -> session.getString("seatType"))
            .formParam("seatPref", session -> session.getString("seatPref"))
            .formParam("reserveFlights.x", "38").formParam("reserveFlights.y", "5");

    public static HttpRequestActionBuilder fillPayment = http("Fill 'Payment Details'")
            .post("/cgi-bin/reservations.pl")
            .formParam("firstName", "#{firstName}")
            .formParam("lastName", "#{lastName}")
            .formParam("address1", "#{address1}")
            .formParam("address2", "#{address2}")
            .formParam("pass1", "#{passengerNames}")
            .formParam("creditCard", "#{creditCardNumber}")
            .formParam("expDate", "#{expDate}")
            .formParam("oldCCOption", "")
            .formParam("numPassengers", "1")
            .formParam("seatType", session -> session.getString("seatType"))
            .formParam("seatPref", session -> session.getString("seatPref"))
            .formParam("outboundFlight", session -> session.getString("outboundFlight"))
            .formParam("advanceDiscount", "0")
            .formParam("returnFlight", session -> session.getString("returnFlight"))
            .formParam("JSFormSubmit", "off").formParam("buyFlights.x", "59")
            .formParam("buyFlights.y", "6").formParam(".cgifields", "saveCC")
            .check(css("small>b").is("Thank you for booking through Web Tours."));
}