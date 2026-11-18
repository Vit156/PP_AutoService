package com.autoservice.repository;

import com.autoservice.domain.OrderStatus;
import com.autoservice.domain.ServiceOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepairOrderRepository extends JpaRepository<ServiceOrder, Long> {
    List<ServiceOrder> findByCurrentStatus(OrderStatus status);
}