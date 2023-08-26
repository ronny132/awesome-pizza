package shop.awesome.pizza.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import shop.awesome.pizza.domain.OrderStatus;
import shop.awesome.pizza.domain.dto.OrderCreationDto;
import shop.awesome.pizza.domain.dto.OrderDto;
import shop.awesome.pizza.domain.dto.OrderUpdateDto;
import shop.awesome.pizza.domain.entity.Order;
import shop.awesome.pizza.domain.map.OrderMapper;
import shop.awesome.pizza.repository.OrderRepository;
import shop.awesome.pizza.service.OrderService;

import java.util.List;
import java.util.Optional;

import static shop.awesome.pizza.domain.map.OrderMapper.toDto;
import static shop.awesome.pizza.domain.map.OrderMapper.toEntity;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository repository;

    @Override
    public List<OrderDto> listAll() {
        return toDto(repository.findAll());
    }

    @Override
    public List<OrderDto> findAllByStatus(OrderStatus status) {
        return toDto(repository.findAllByStatusOrderByCreateDate(status));
    }

    @Override
    public Optional<OrderDto> findByCode(long code) {
        Optional<Order> orderOptional = repository.findById(code);
        if (orderOptional.isEmpty()) return Optional.empty();
        return Optional.of(toDto(orderOptional.get()));
    }

    @Override
    public Optional<OrderDto> updateOrder(long code, OrderUpdateDto order) {
        Optional<Order> orderOptional = repository.findById(code);
        if (orderOptional.isEmpty()) return Optional.empty();
        Order merged = OrderMapper.merge(orderOptional.get(), order);
        return Optional.of(toDto(repository.save(merged)));
    }

    @Override
    public OrderDto createOrder(OrderCreationDto order) {
        Order save = repository.save(toEntity(order));
        return toDto(save);
    }
}
