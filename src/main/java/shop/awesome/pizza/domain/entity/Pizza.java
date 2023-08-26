package shop.awesome.pizza.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PIZZA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pizza {
    @Id
    private String name;
    private double cost;
}
