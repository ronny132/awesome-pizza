package shop.awesome.pizza.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import shop.awesome.pizza.domain.OrderStatus;

@Builder
@Data
public class OrderUpdateDto {
    @NotNull
    private Long code;
    @NotNull
    private OrderStatus status;
}
