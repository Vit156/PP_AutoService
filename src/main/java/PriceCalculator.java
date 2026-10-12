import java.math.BigDecimal;

public class PriceCalculator {
    private static final BigDecimal HIGH_COST_THRESHOLD = new BigDecimal("10000");
    private static final BigDecimal HIGH_COST_DISCOUNT_PERCENTAGE = new BigDecimal("5");
    private static final BigDecimal MINIMUM_PRICE = new BigDecimal("500");
    private static final BigDecimal PERCENTAGE_DIVISOR = new BigDecimal("100");

    public BigDecimal calculateCommercialPrice(ServiceOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Service order cannot be null");
        }

        BigDecimal baseCost = order.calculateTotalCost();
        BigDecimal discountPercentage = BigDecimal.ZERO;

        if (order.getClient() != null) {
            discountPercentage = order.getClient().getLoyaltyDiscountPercentage();
        }
        if (baseCost.compareTo(HIGH_COST_THRESHOLD) > 0) {
            discountPercentage = discountPercentage.add(HIGH_COST_DISCOUNT_PERCENTAGE);
        }

        BigDecimal discount = baseCost.multiply(discountPercentage).divide(PERCENTAGE_DIVISOR);
        BigDecimal finalPrice = baseCost.subtract(discount);
        return finalPrice.compareTo(MINIMUM_PRICE) < 0 ? MINIMUM_PRICE : finalPrice;
    }
}
