package shop.awesome.pizza.e2e;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import shop.awesome.pizza.domain.OrderStatus;
import shop.awesome.pizza.domain.dto.OrderCreationDto;
import shop.awesome.pizza.domain.dto.OrderUpdateDto;
import shop.awesome.pizza.domain.entity.Pizza;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Sql("/db/pizza_data.sql")
class PizzaApiE2ETest {
    private static final String ORDERS_API = "/orders";
    private static final String SINGLE_ORDER_API = "/orders/{code}";
    private static final String TEST_CUSTOMER_NAME = "ciccio customer";
    private static final Pizza PIZZA_MARGHERITA = Pizza.builder().name("margherita").cost(6).build();
    private static final String PIZZA_API = "/pizzas";

    @LocalServerPort
    int port;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost/v1";
        RestAssured.port = port;
    }


    @Test
    void list_orders_should_return_200() {
        given()
                .when()
                .get(ORDERS_API)
                .then()
                .log().ifError()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void list_orders_should_return_200_when_status_passed() {
        given()
                .when()
                .queryParam("status", OrderStatus.CREATED)
                .get(ORDERS_API)
                .then()
                .log().ifError()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void list_orders_should_return_400_when_invalid_status_passed() {
        given()
                .when()
                .queryParam("status", "invalid")
                .get(ORDERS_API)
                .then()
                .log().ifError()
                .statusCode(400)
                .contentType(ContentType.JSON);
    }

    @Test
    void get_single_order_should_return_404_when_nonexisting_order() {
        given()
                .when()
                .pathParam("code", 0)
                .get(SINGLE_ORDER_API)
                .then()
                .log().ifError()
                .statusCode(404)
                .contentType(ContentType.JSON);
    }

    @Test
    @Sql("/db/orders_base_data.sql")
    void get_single_order_should_return_200_when_existing_order() {
        int code = 1;
        given()
                .when()
                .pathParam("code", code)
                .get(SINGLE_ORDER_API)
                .then()
                .log().ifError()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("code", is(code));
    }

    @Test
    void create_order_should_return_200_when_valid_order() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(
                        OrderCreationDto.builder()
                                .customerName(TEST_CUSTOMER_NAME)
                                .pizzaList(List.of(PIZZA_MARGHERITA))
                        .build()
                )
                .post(ORDERS_API)
                .then()
                .log().ifError()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("customerName", is(TEST_CUSTOMER_NAME))
                .body("pizzaList[0].name", is("margherita"))
                .body("pizzaList[0].cost", is(6f))
                .body("code", notNullValue());
    }

    @Test
    void create_order_should_return_400_when_pizze_missing() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(
                        OrderCreationDto.builder()
                                .customerName(TEST_CUSTOMER_NAME)
                                .build()
                )
                .post(ORDERS_API)
                .then()
                .log().ifError()
                .statusCode(400)
                .contentType(ContentType.JSON);
    }

    @Test
    void create_order_should_return_400_when_customer_missing() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .body(
                        OrderCreationDto.builder()
                                .pizzaList(List.of(PIZZA_MARGHERITA))
                                .build()
                )
                .post(ORDERS_API)
                .then()
                .log().ifError()
                .statusCode(400)
                .contentType(ContentType.JSON);
    }

    @Test
    @Sql({"/db/pizza_data.sql", "/db/orders_data.sql"})
    void update_order_should_return_200_when_valid_order_and_valid_dto() {
        long orderCode = 1;
        given()
                .contentType(ContentType.JSON)
                .when()
                .pathParam("code", orderCode)
                .body(
                        OrderUpdateDto.builder()
                                .code(orderCode)
                                .status(OrderStatus.COOKING)
                                .build()
                )
                .patch(SINGLE_ORDER_API)
                .then()
                .log().ifError()
                .statusCode(200);
    }

    @Test
    void update_order_should_return_404_when_order_not_found() {
        long orderCode = 0;
        given()
                .contentType(ContentType.JSON)
                .when()
                .pathParam("code", orderCode)
                .body(
                        OrderUpdateDto.builder()
                                .code(orderCode)
                                .status(OrderStatus.COOKING)
                                .build()
                )
                .patch(SINGLE_ORDER_API)
                .then()
                .log().ifError()
                .statusCode(404);
    }

    @Test
    @Sql({"/db/pizza_data.sql", "/db/orders_data.sql"})
    void update_order_should_return_400_when_status_null() {
        long orderCode = 1;
        given()
                .contentType(ContentType.JSON)
                .when()
                .pathParam("code", orderCode)
                .body(
                        OrderUpdateDto.builder()
                                .code(orderCode)
                                .build()
                )
                .patch(SINGLE_ORDER_API)
                .then()
                .log().ifError()
                .statusCode(400);
    }

    @Test
    @Sql({"/db/pizza_data.sql", "/db/orders_data.sql"})
    void update_order_should_return_400_when_code_null() {
        long orderCode = 1;
        given()
                .contentType(ContentType.JSON)
                .when()
                .pathParam("code", orderCode)
                .body(
                        OrderUpdateDto.builder()
                                .status(OrderStatus.COOKING)
                                .build()
                )
                .patch(SINGLE_ORDER_API)
                .then()
                .log().ifError()
                .statusCode(400);
    }

    @Test
    void listAllPizza_should_return_200() {
        List<Pizza> list = given()
                .when()
                .get(PIZZA_API)
                .then()
                .log().ifError()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        Assertions.assertThat(list).hasSize(5);
    }

}
