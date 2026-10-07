package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductEndpointTest {
  @LocalServerPort
  int port;

  @BeforeEach
  void configureRestAssured() {
    RestAssured.port = port;
  }


  @Test
  void shouldExerciseProductCrudAndValidation() {
    given().when().get("product").then().statusCode(200)
        .body(containsString("TONSTAD"), containsString("KALLAX"), containsString("BESTÅ"));

    given().when().get("product/2").then().statusCode(200).body(containsString("KALLAX"));
    given().when().get("product/9999").then().statusCode(404);

    String create = "{\"name\":\"PRODUCT-TEST\",\"description\":\"test\",\"price\":10.50,\"stock\":8}";

    Integer createdProductId =
            given()
                    .contentType("application/json")
                    .body(create)
                    .when()
                    .post("product")
                    .then()
                    .statusCode(201)
                    .body(containsString("PRODUCT-TEST"))
                    .extract()
                    .path("id");

    given().contentType("application/json").body("{\"id\":2,\"name\":\"INVALID\"}")
        .when().post("product").then().statusCode(422);

    String update = "{\"name\":\"PRODUCT-UPDATED\",\"description\":\"updated\",\"price\":12.50,\"stock\":9}";
    given().contentType("application/json").body(update)
        .when().put("product/2").then().statusCode(200).body(containsString("PRODUCT-UPDATED"));

    given().contentType("application/json").body("{\"description\":\"missing name\"}")
        .when().put("product/2").then().statusCode(422);
    given().contentType("application/json").body(update)
        .when().put("product/9999").then().statusCode(404);

    given()
            .when()
            .delete("product/" + createdProductId)
            .then()
            .statusCode(204);

    given()
            .when()
            .get("product")
            .then()
            .statusCode(200)
            .body(not(containsString("PRODUCT-TEST")));

    given().when().delete("product/9999").then().statusCode(404);
  }
}
