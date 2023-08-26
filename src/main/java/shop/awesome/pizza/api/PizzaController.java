package shop.awesome.pizza.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.awesome.pizza.domain.entity.Pizza;
import shop.awesome.pizza.repository.PizzaRepository;

import java.util.List;

@RestController
@RequestMapping("/v1/pizzas")
@RequiredArgsConstructor
public class PizzaController {
    private final PizzaRepository repository;

    @GetMapping
    @Operation(
            summary = "Retrieve all pizza",
            description = "Retrieve all pizza available in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK")
            }
    )
    List<Pizza> listAllPizza() {
        return repository.findAll();
    }
}
