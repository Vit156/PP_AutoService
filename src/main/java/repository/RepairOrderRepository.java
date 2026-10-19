package repository;

import domain.OrderStatus;
import domain.ServiceOrder;

import java.util.List;
import java.util.Optional;

public interface RepairOrderRepository {
    void save(ServiceOrder order);

    Optional<ServiceOrder> findById(Long id);

    List<ServiceOrder> findByStatus(OrderStatus status);

    List<ServiceOrder> findAll();
}
