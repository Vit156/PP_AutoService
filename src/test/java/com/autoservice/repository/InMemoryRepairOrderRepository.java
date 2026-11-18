package com.autoservice.repository;

import com.autoservice.domain.OrderStatus;
import com.autoservice.domain.ServiceOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryRepairOrderRepository {
    private final Map<Long, ServiceOrder> orders = new HashMap<>();

    public void save(ServiceOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Service order cannot be null");
        }
        if (order.getId() == null) {
            throw new IllegalArgumentException("Service order ID cannot be null");
        }
        orders.put(order.getId(), order);
    }

    public Optional<ServiceOrder> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(orders.get(id));
    }

    public List<ServiceOrder> findAll() {
        return List.copyOf(orders.values());
    }

    public List<ServiceOrder> findByStatus(OrderStatus status) {
        return orders.values().stream()
                .filter(order -> order.getStatus() == status)
                .collect(Collectors.toList());
    }
}