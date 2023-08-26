package shop.awesome.pizza.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.awesome.pizza.domain.OrderStatus;
import shop.awesome.pizza.domain.dto.OrderCreationDto;
import shop.awesome.pizza.domain.dto.OrderDto;
import shop.awesome.pizza.domain.dto.OrderUpdateDto;
import shop.awesome.pizza.domain.entity.Order;
import shop.awesome.pizza.domain.entity.Pizza;
import shop.awesome.pizza.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static shop.awesome.pizza.domain.map.OrderMapper.toDto;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplUnitTest {
    private static final Pizza PIZZA_MARGHERITA = Pizza.builder().name("margherita").cost(6).build();
    private static final String TEST_CUSTOMER_NAME = "ciccio customer";
    private static final long TEST_CODE = 1;
    @Mock
    private OrderRepository repository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void listAll_should_return_emptyList() {
        when(repository.findAll()).thenReturn(List.of());

        Assertions.assertThat(orderService.listAll()).isEmpty();
    }

    @Test
    void listAll_should_return_valid_list() {
        Order order = Order.builder()
                .code(TEST_CODE)
                .customerName(TEST_CUSTOMER_NAME)
                .status(OrderStatus.CREATED)
                .createDate(LocalDateTime.now())
                .pizzaList(List.of(PIZZA_MARGHERITA))
                .build();
        when(repository.findAll()).thenReturn(List.of(order));

        Assertions.assertThat(orderService.listAll()).containsOnly(toDto(order));
    }

    @Test
    void findAllByStatus_should_return_valid_list() {
        Order order = Order.builder()
                .code(TEST_CODE)
                .customerName(TEST_CUSTOMER_NAME)
                .status(OrderStatus.CREATED)
                .createDate(LocalDateTime.now())
                .pizzaList(List.of(PIZZA_MARGHERITA))
                .build();
        when(repository.findAllByStatusOrderByCreateDate(OrderStatus.CREATED)).thenReturn(List.of(order));
        when(repository.findAllByStatusOrderByCreateDate(OrderStatus.COOKING)).thenReturn(List.of());

        Assertions.assertThat(orderService.findAllByStatus(OrderStatus.CREATED)).containsOnly(toDto(order));
        Assertions.assertThat(orderService.findAllByStatus(OrderStatus.COOKING)).isEmpty();
    }

    @Test
    void findByCode_should_return_empty() {
        when(repository.findById(TEST_CODE)).thenReturn(Optional.empty());

        Assertions.assertThat(orderService.findByCode(TEST_CODE)).isEmpty();
    }

    @Test
    void findByCode_should_return_valid_object() {
        Order order = Order.builder()
                .code(TEST_CODE)
                .customerName(TEST_CUSTOMER_NAME)
                .status(OrderStatus.CREATED)
                .createDate(LocalDateTime.now())
                .pizzaList(List.of(PIZZA_MARGHERITA))
                .build();
        when(repository.findById(TEST_CODE)).thenReturn(Optional.of(order));

        Optional<OrderDto> orderDto = orderService.findByCode(TEST_CODE);
        Assertions.assertThat(orderDto).isPresent();
        Assertions.assertThat(orderDto).get().isEqualTo(toDto(order));
    }

    @Test
    void updateOrder_should_return_empty() {
        when(repository.findById(TEST_CODE)).thenReturn(Optional.empty());

        Assertions.assertThat(orderService.updateOrder(TEST_CODE, OrderUpdateDto.builder()
                .code(TEST_CODE)
                .status(OrderStatus.COOKING)
                .build())).isEmpty();
    }

    @Test
    void updateOrder_should_return_valid() {
        Order order = Order.builder()
                .code(TEST_CODE)
                .customerName(TEST_CUSTOMER_NAME)
                .status(OrderStatus.CREATED)
                .createDate(LocalDateTime.now())
                .pizzaList(List.of(PIZZA_MARGHERITA))
                .build();
        when(repository.findById(TEST_CODE)).thenReturn(Optional.of(order));
        when(repository.save(order)).thenReturn(order);

        Optional<OrderDto> orderDto = orderService.updateOrder(TEST_CODE, OrderUpdateDto.builder()
                .code(TEST_CODE)
                .status(OrderStatus.COOKING)
                .build());
        Assertions.assertThat(orderDto).isPresent();
        order.setStatus(OrderStatus.COOKING);
        Assertions.assertThat(orderDto).get().isEqualTo(toDto(order));
    }

    @Test
    void createOrder_should_return_valid() {
        Order order = Order.builder()
                .code(TEST_CODE)
                .customerName(TEST_CUSTOMER_NAME)
                .status(OrderStatus.CREATED)
                .createDate(LocalDateTime.now())
                .pizzaList(List.of(PIZZA_MARGHERITA))
                .build();
        when(repository.save(any())).thenReturn(order);

        OrderDto orderDto = orderService.createOrder(OrderCreationDto.builder()
                        .customerName(TEST_CUSTOMER_NAME)
                        .pizzaList(List.of(PIZZA_MARGHERITA))
                .build());
        Assertions.assertThat(orderDto).isEqualTo(toDto(order));
    }
}
