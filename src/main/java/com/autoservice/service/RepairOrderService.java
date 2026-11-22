package com.autoservice.service;

import com.autoservice.domain.OrderStatus;
import com.autoservice.domain.ServiceOrder;
import com.autoservice.pricing.PriceCalculator;
import com.autoservice.repository.RepairOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.autoservice.domain.Job;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class RepairOrderService {
    private final RepairOrderRepository repairOrderRepository;
    private final PriceCalculator priceCalculator;

    public RepairOrderService(RepairOrderRepository repairOrderRepository, PriceCalculator priceCalculator) {
        this.repairOrderRepository = Objects.requireNonNull(repairOrderRepository, "RepairOrderRepository cannot be null");
        this.priceCalculator = Objects.requireNonNull(priceCalculator, "PriceCalculator cannot be null");
    }

    @Transactional
    public ServiceOrder saveOrder(ServiceOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Service order cannot be null");
        }
        return repairOrderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public ServiceOrder getOrderById(Long id) {
        return repairOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service order not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<ServiceOrder> getAllOrders() {
        return repairOrderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ServiceOrder> findOrdersByStatus(OrderStatus status) {
        return repairOrderRepository.findByCurrentStatus(status);
    }

    @Transactional
    public BigDecimal completeServiceOrder(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }

        ServiceOrder order = repairOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Service order not found: " + orderId));

        order.completeOrder();
        BigDecimal commercialPrice = priceCalculator.calculateCommercialPrice(order);
        order.setFinalPrice(commercialPrice);
        repairOrderRepository.save(order);
        return commercialPrice;
    }

    @Transactional
    public void addJobsBatch(Long orderId, List<Job> jobs) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (jobs == null || jobs.isEmpty()) {
            throw new IllegalArgumentException("Jobs list cannot be null or empty");
        }

        ServiceOrder order = repairOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Service order not found: " + orderId));

        jobs.forEach(job -> {
            order.addJob(job);
            repairOrderRepository.saveAndFlush(order);
        });
    }

}