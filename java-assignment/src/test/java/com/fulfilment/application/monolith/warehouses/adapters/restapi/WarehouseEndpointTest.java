package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WarehouseEndpointTest {
  @LocalServerPort
  int port;

  @BeforeEach
  void configureRestAssured() {
    RestAssured.port = port;
  }


  @Test
  void shouldListActiveWarehouses() {
    given().when().get("warehouse").then().statusCode(200)
        .body(containsString("MWH.001"), containsString("MWH.012"), containsString("MWH.023"));
  }

  @Test
  void shouldCreateGetAndArchiveWarehouse() {
    String request = "{\"businessUnitCode\":\"MWH.TEST\",\"location\":\"VETSBY-001\",\"capacity\":50,\"stock\":20}";

    given().contentType("application/json").body(request)
        .when().post("warehouse").then().statusCode(200).body(containsString("MWH.TEST"));

    given().when().get("warehouse/MWH.TEST").then().statusCode(200).body(containsString("MWH.TEST"));

    given().when().delete("warehouse/MWH.TEST").then().statusCode(200);
    given().when().get("warehouse/MWH.TEST").then().statusCode(404);
  }

  @Test
  void shouldReplaceWarehouseAndPreserveTheBusinessUnitCode() {
    String request = "{\"businessUnitCode\":\"MWH.023\",\"location\":\"TILBURG-001\",\"capacity\":35,\"stock\":27}";

    given().contentType("application/json").body(request)
        .when().post("warehouse/MWH.023/replacement")
        .then().statusCode(200)
        .body(containsString("MWH.023"), containsString("TILBURG-001"));
  }

  @Test
  void shouldRejectInvalidWarehouseCreation() {
    String request = "{\"businessUnitCode\":\"MWH.BAD\",\"location\":\"UNKNOWN\",\"capacity\":10,\"stock\":5}";
    given().contentType("application/json").body(request)
        .when().post("warehouse").then().statusCode(400);
  }

  @Test
  void shouldReturn404ForUnknownWarehouse() {
    given()
            .when()
            .get("warehouse/UNKNOWN-WAREHOUSE")
            .then()
            .statusCode(404);
  }

  @Test
  void shouldReturn404WhenArchivingUnknownWarehouse() {
    given()
            .when()
            .delete("warehouse/UNKNOWN-WAREHOUSE")
            .then()
            .statusCode(404);
  }
}
