import java.math.BigDecimal;
import java.util.Objects;

public class RepairOrderService {
    private final RepairOrderRepository repairOrderRepository;
    private final PriceCalculator priceCalculator;

    public RepairOrderService(RepairOrderRepository repairOrderRepository, PriceCalculator priceCalculator) {
        this.repairOrderRepository = Objects.requireNonNull(repairOrderRepository, "RepairOrderRepository cannot be null");
        this.priceCalculator = Objects.requireNonNull(priceCalculator, "PriceCalculator cannot be null");
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