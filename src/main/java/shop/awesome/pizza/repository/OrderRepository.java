package shop.awesome.pizza.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import shop.awesome.pizza.domain.OrderStatus;
import shop.awesome.pizza.domain.entity.Order;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findAll();

    List<Order> findAllByStatusOrderByCreateDate(OrderStatus status);
}
