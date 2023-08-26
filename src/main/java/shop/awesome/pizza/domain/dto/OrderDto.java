package shop.awesome.pizza.domain.dto;

import lombok.Builder;
import lombok.Data;
import shop.awesome.pizza.domain.OrderStatus;
import shop.awesome.pizza.domain.entity.Pizza;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class OrderDto {
    private long code;
    private String customerName;
    private OrderStatus status;
    private LocalDateTime createDate;
    private List<Pizza> pizzaList;
    private double totalCost;
}
