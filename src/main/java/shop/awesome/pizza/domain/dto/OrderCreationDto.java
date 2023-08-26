package shop.awesome.pizza.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import shop.awesome.pizza.domain.entity.Pizza;

import java.util.List;

@Builder
@Data
public class OrderCreationDto {
    @NotBlank
    private String customerName;
    @NotEmpty
    @NotNull
    private List<Pizza> pizzaList;
}
