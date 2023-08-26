package shop.awesome.pizza.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shop.awesome.pizza.domain.entity.Pizza;

import java.util.List;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, String> {
    List<Pizza> findAll();
}
