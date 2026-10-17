import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PriceCalculatorTest {
    private final PriceCalculator priceCalculator = new PriceCalculator();

    @Test
    void calculateCommercialPrice_regularClientBelowThreshold_noDiscountApplied() {
        // Given
        ServiceOrder order = createOrderWithCost("1000", false);

        // When
        BigDecimal actualPrice = priceCalculator.calculateCommercialPrice(order);

        // Then
        assertEquals(0, new BigDecimal("1000.00").compareTo(actualPrice));
    }

    @Test
    void calculateCommercialPrice_baseCostBelowThreshold_appliesOnlyLoyaltyDiscount() {
        // Given
        ServiceOrder order = createOrderWithCost("1000", true);

        // When
        BigDecimal actualPrice = priceCalculator.calculateCommercialPrice(order);

        // Then
        assertEquals(0, new BigDecimal("970.00").compareTo(actualPrice));
    }

    @Test
    void calculateCommercialPrice_baseCostAboveThreshold_appliesExtraDiscount() {
        // Given
        ServiceOrder order = createOrderWithCost("12000", true);

        // When
        BigDecimal actualPrice = priceCalculator.calculateCommercialPrice(order);

        // Then
        assertEquals(0, new BigDecimal("11040.00").compareTo(actualPrice));
    }

    @Test
    void calculateCommercialPrice_priceDropsBelowMinimum_returnsMinimumPrice() {
        // Given
        ServiceOrder order = createOrderWithCost("510", true);

        // When
        BigDecimal actualPrice = priceCalculator.calculateCommercialPrice(order);

        // Then
        assertEquals(0, new BigDecimal("500.00").compareTo(actualPrice));
    }

    @Test
    void calculateCommercialPrice_nullOrder_throwsIllegalArgumentException() {
        // Given
        ServiceOrder order = null;

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> priceCalculator.calculateCommercialPrice(order));

        // Then
        assertEquals("Service order cannot be null", exception.getMessage());
    }

    private ServiceOrder createOrderWithCost(String cost, boolean loyalClient) {
        Client client = new Client(1L, "Test Client");
        if (loyalClient) {
            client.recordCompletedRepair();
            client.recordCompletedRepair();
            client.recordCompletedRepair();
        }
        Car car = new Car("TEST-VIN", "Test Model", "Test Brand", 2024, client);
        ServiceOrder order = new ServiceOrder(car);
        order.addJob(new Job(1L, "Repair", Specialization.ENGINE_REPAIR,
                new BigDecimal(cost), BigDecimal.ZERO));
        return order;
    }
}