package shop.awesome.pizza.domain.map;

import shop.awesome.pizza.domain.OrderStatus;
import shop.awesome.pizza.domain.dto.OrderCreationDto;
import shop.awesome.pizza.domain.dto.OrderDto;
import shop.awesome.pizza.domain.dto.OrderUpdateDto;
import shop.awesome.pizza.domain.entity.Order;
import shop.awesome.pizza.domain.entity.Pizza;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class OrderMapper {
    public static List<OrderDto> toDto(List<Order> list) {
        return list.stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    public static OrderDto toDto(Order order) {
        return OrderDto.builder()
                .code(order.getCode())
                .customerName(order.getCustomerName())
                .createDate(order.getCreateDate() != null ? order.getCreateDate().truncatedTo(ChronoUnit.SECONDS) : null)
                .pizzaList(new ArrayList<>(order.getPizzaList())) // su PersistenceBag di Jakarta non funziona il metodo equals
                .status(order.getStatus())
                .totalCost(
                        order.getPizzaList()
                                .stream()
                                .map(Pizza::getCost)
                                .reduce(0d, Double::sum)
                )
                .build();
    }

    public static Order toEntity(OrderCreationDto dto) {
        return Order.builder()
                .customerName(dto.getCustomerName())
                .pizzaList(dto.getPizzaList())
                .status(OrderStatus.CREATED)
                .createDate(LocalDateTime.now())
                .build();
    }

    public static Order merge(Order order, OrderUpdateDto updateDto) {
        order.setStatus(updateDto.getStatus());
        return order;
    }
}
