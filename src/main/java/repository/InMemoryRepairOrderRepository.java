package repository;

import domain.OrderStatus;
import domain.ServiceOrder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryRepairOrderRepository implements RepairOrderRepository {
    private final Map<Long, ServiceOrder> orders = new ConcurrentHashMap<>();

    @Override
    public void save(ServiceOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Service order cannot be null");
        }
        if (order.getId() == null) {
            throw new IllegalArgumentException("Service order ID cannot be null");
        }

        orders.put(order.getId(), order);
    }

    @Override
    public Optional<ServiceOrder> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<ServiceOrder> findAll() {
        return List.copyOf(orders.values());
    }

    @Override
    public List<ServiceOrder> findByStatus(OrderStatus status) {
        return orders.values().stream()
                .filter(order -> order.getStatus() == status)
                .collect(Collectors.toList());
    }
}
