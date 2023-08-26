package shop.awesome.pizza.integration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import shop.awesome.pizza.domain.OrderStatus;
import shop.awesome.pizza.domain.dto.OrderCreationDto;
import shop.awesome.pizza.domain.dto.OrderDto;
import shop.awesome.pizza.domain.dto.OrderUpdateDto;
import shop.awesome.pizza.domain.entity.Pizza;
import shop.awesome.pizza.service.OrderService;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Sql("/db/pizza_data.sql")
class OrderServiceIntegrationTest {
    private static final String TEST_CUSTOMER_NAME = "ciccio customer";
    private static final Pizza PIZZA_MARGHERITA = Pizza.builder().name("margherita").cost(6).build();
    private static final Pizza PIZZA_DIAVOLA = Pizza.builder().name("diavola").cost(7.5).build();
    @Autowired
    private OrderService service;

    @Test
    @Sql({"/db/pizza_data.sql", "/db/orders_data.sql"})
    void list_orders_should_return_list_when_loaded_from_database() {
        List<OrderDto> orders = service.listAll();
        Assertions.assertThat(orders).map(OrderDto::getCustomerName).containsOnly(TEST_CUSTOMER_NAME);
        Assertions.assertThat(orders).map(OrderDto::getPizzaList).map(list -> list.get(0))
                .containsOnly(PIZZA_DIAVOLA);
    }

    @Test
    void creating_updating_and_fetching_should_work() {
        OrderDto createdOrder = service.createOrder(OrderCreationDto.builder()
                .customerName(TEST_CUSTOMER_NAME)
                .pizzaList(List.of(PIZZA_MARGHERITA))
                .build()
        );

        Assertions.assertThat(createdOrder.getCode()).isGreaterThan(0);
        Assertions.assertThat(createdOrder.getCreateDate()).isNotNull();

        List<OrderDto> createdOrders = service.findAllByStatus(OrderStatus.CREATED);
        Assertions.assertThat(createdOrders).containsOnly(createdOrder);

        List<OrderDto> cookingOrders = service.findAllByStatus(OrderStatus.COOKING);
        Assertions.assertThat(cookingOrders).isEmpty();

        service.updateOrder(createdOrder.getCode(), OrderUpdateDto.builder()
                .code(createdOrder.getCode())
                .status(OrderStatus.COOKING)
                .build());

        createdOrders = service.findAllByStatus(OrderStatus.CREATED);
        Assertions.assertThat(createdOrders).isEmpty();

        cookingOrders = service.findAllByStatus(OrderStatus.COOKING);
        Assertions.assertThat(cookingOrders).hasSize(1);
    }

    @Test
    void creating_multiple_orders_should_assign_different_codes() {
        OrderDto createdOrder1 = service.createOrder(OrderCreationDto.builder()
                .customerName(TEST_CUSTOMER_NAME)
                .pizzaList(List.of(PIZZA_MARGHERITA))
                .build()
        );

        OrderDto createdOrder2 = service.createOrder(OrderCreationDto.builder()
                .customerName(TEST_CUSTOMER_NAME)
                .pizzaList(List.of(PIZZA_MARGHERITA))
                .build()
        );

        Assertions.assertThat(createdOrder1.getCode()).isNotEqualTo(createdOrder2.getCode());
    }
}
