package service;

import domain.OrderStatus;
import domain.ServiceOrder;
import repository.RepairOrderRepository;
import pricing.PriceCalculator;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class RepairOrderService {
    private final RepairOrderRepository repairOrderRepository;
    private final PriceCalculator priceCalculator;

    public RepairOrderService(RepairOrderRepository repairOrderRepository, PriceCalculator priceCalculator) {
        this.repairOrderRepository = Objects.requireNonNull(repairOrderRepository, "repository.RepairOrderRepository cannot be null");
        this.priceCalculator = Objects.requireNonNull(priceCalculator, "pricing.PriceCalculator cannot be null");
    }

    public ServiceOrder saveOrder(ServiceOrder order) {
        repairOrderRepository.save(order);
        return order;
    }

    public ServiceOrder getOrderById(Long id) {
        return repairOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Service order not found: " + id));
    }

    public List<ServiceOrder> findOrdersByStatus(OrderStatus status) {
        return repairOrderRepository.findByStatus(status);
    }

    public BigDecimal completeServiceOrder(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }

        ServiceOrder order = repairOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Service order not found: " + orderId));

        order.completeOrder();
        BigDecimal commercialPrice = priceCalculator.calculateCommercialPrice(order);
        repairOrderRepository.save(order);
        return commercialPrice;
    }
}