package com.fulfilment.application.monolith.fulfillment;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FulfillmentResourceTest {
  @LocalServerPort
  int port;

  @BeforeEach
  void configureRestAssured() {
    RestAssured.port = port;
  }


  @Test
  void shouldCreateAndRejectDuplicateFulfillment() {
    String request = "{\"storeId\":1,\"productId\":1,\"warehouseBusinessUnitCode\":\"MWH.001\"}";

    given().contentType("application/json").body(request)
        .when().post("/fulfillment")
        .then().statusCode(201).body(containsString("MWH.001"));

    given().contentType("application/json").body(request)
        .when().post("/fulfillment")
        .then().statusCode(400);
  }

  @Test
  void shouldRejectUnknownWarehouse() {
    given().contentType("application/json")
        .body("{\"storeId\":1,\"productId\":1,\"warehouseBusinessUnitCode\":\"UNKNOWN\"}")
        .when().post("/fulfillment")
        .then().statusCode(404);
  }
}
