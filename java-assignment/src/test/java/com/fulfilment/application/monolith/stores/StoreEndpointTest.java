package com.fulfilment.application.monolith.stores;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StoreEndpointTest {
  @LocalServerPort
  int port;

  @BeforeEach
  void configureRestAssured() {
    RestAssured.port = port;
  }


  @Test
  void shouldExerciseStoreCrudAndValidation() {
    given().when().get("/store").then().statusCode(200)
        .body(containsString("TONSTAD"), containsString("KALLAX"));

    given().when().get("/store/2").then().statusCode(200).body(containsString("KALLAX"));
    given().when().get("/store/9999").then().statusCode(404);

    String create = "{\"name\":\"TEST-STORE\",\"quantityProductsInStock\":7}";
    given().contentType("application/json").body(create)
        .when().post("/store").then().statusCode(201).body(containsString("TEST-STORE"));

    given().contentType("application/json").body("{\"id\":2,\"name\":\"INVALID\"}")
        .when().post("/store").then().statusCode(422);

    String update = "{\"name\":\"UPDATED-STORE\",\"quantityProductsInStock\":12}";
    given().contentType("application/json").body(update)
        .when().put("/store/2").then().statusCode(200);
    given().contentType("application/json").body(update)
        .when().patch("/store/2").then().statusCode(200);

    given().contentType("application/json").body("{\"quantityProductsInStock\":1}")
        .when().put("/store/2").then().statusCode(422);
    given().contentType("application/json").body("{\"quantityProductsInStock\":1}")
        .when().patch("/store/2").then().statusCode(422);

    given().contentType("application/json").body(update)
        .when().put("/store/9999").then().statusCode(404);
    given().contentType("application/json").body(update)
        .when().patch("/store/9999").then().statusCode(404);

    given().when().delete("/store/3").then().statusCode(204);
    given().when().delete("/store/9999").then().statusCode(404);
  }
}
