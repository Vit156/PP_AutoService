import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class PriceCalculator {
    private static final BigDecimal HIGH_COST_THRESHOLD = new BigDecimal("10000");
    private static final BigDecimal HIGH_COST_DISCOUNT_PERCENTAGE = new BigDecimal("5");
    private static final BigDecimal MINIMUM_PRICE = new BigDecimal("500.00");
    private static final BigDecimal PERCENTAGE_DIVISOR = new BigDecimal("100");

    public BigDecimal calculateCommercialPrice(ServiceOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Service order cannot be null");
        }

        BigDecimal baseCost = Optional.ofNullable(order.calculateTotalCost())
                .orElse(BigDecimal.ZERO);

        BigDecimal discountPercentage = Optional.ofNullable(order.getClient())
                .map(Client::getLoyaltyDiscountPercentage)
                .orElse(BigDecimal.ZERO);

        if (baseCost.compareTo(HIGH_COST_THRESHOLD) > 0) {
            discountPercentage = discountPercentage.add(HIGH_COST_DISCOUNT_PERCENTAGE);
        }

        BigDecimal discount = baseCost.multiply(discountPercentage)
                .divide(PERCENTAGE_DIVISOR, 2, RoundingMode.HALF_UP);

        BigDecimal finalPrice = baseCost.subtract(discount)
                .setScale(2, RoundingMode.HALF_UP);

        return finalPrice.max(MINIMUM_PRICE);
    }
}