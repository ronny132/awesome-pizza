package shop.awesome.pizza.service;

import shop.awesome.pizza.domain.OrderStatus;
import shop.awesome.pizza.domain.dto.OrderCreationDto;
import shop.awesome.pizza.domain.dto.OrderDto;
import shop.awesome.pizza.domain.dto.OrderUpdateDto;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<OrderDto> listAll();

    List<OrderDto> findAllByStatus(OrderStatus status);

    Optional<OrderDto> findByCode(long code);

    Optional<OrderDto> updateOrder(long code, OrderUpdateDto order);

    OrderDto createOrder(OrderCreationDto order);
}
