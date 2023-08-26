package shop.awesome.pizza.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import shop.awesome.pizza.domain.OrderStatus;
import shop.awesome.pizza.domain.dto.OrderCreationDto;
import shop.awesome.pizza.domain.dto.OrderDto;
import shop.awesome.pizza.domain.dto.OrderUpdateDto;
import shop.awesome.pizza.service.OrderService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
class OrdersController {
    private final OrderService orderService;

    @GetMapping
    @Operation(
            summary = "Retrieve all orders",
            description = "Retrieve all orders available in the system. You can optionally specify a status",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK")
            }
    )
    List<OrderDto> listAllOrders(@RequestParam(required = false) OrderStatus status) {
        if (status == null) {
            return orderService.listAll();
        }
        return orderService.findAllByStatus(status);
    }

    @GetMapping(value = "/{code}")
    @Operation(
            summary = "Find order by code",
            description = "Find specific order by code",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "No existing order found with provided code")
            }
    )
    OrderDto getOrder(@PathVariable long code) {
        return orderService.findByCode(code).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
    }

    @PostMapping
    @Operation(
            summary = "Create new order",
            description = "Create and persist new order",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order created"),
                    @ApiResponse(responseCode = "400", description = "Body was invalid")
            }
    )
    OrderDto createOrder(@Valid @RequestBody OrderCreationDto order) {
        return orderService.createOrder(order);
    }

    @PatchMapping("/{code}")
    @Operation(
            summary = "Update order status",
            description = "Update order by changing its status",
            responses = {
                    @ApiResponse(responseCode = "204", description = "No content. Order updated"),
                    @ApiResponse(responseCode = "400", description = "Body was invalid"),
                    @ApiResponse(responseCode = "404", description = "No existing order found with provided code")
            }
    )
    ResponseEntity<Void> updateOrder(@PathVariable long code, @Valid @RequestBody OrderUpdateDto order) {
        try {
            Optional<OrderDto> result = orderService.updateOrder(code, order);
            if (result.isEmpty()) return ResponseEntity.notFound().build();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
