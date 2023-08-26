package shop.awesome.pizza.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.awesome.pizza.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ORDERS")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long code;

    private String customerName;
    private OrderStatus status;
    private LocalDateTime createDate;

    @ManyToMany
    @JoinTable(
            name = "ORDER_PIZZA",
            joinColumns = @JoinColumn(name = "ORDER_CODE"),
            inverseJoinColumns = @JoinColumn(name = "PIZZA_NAME")
    )
    private List<Pizza> pizzaList;
}
