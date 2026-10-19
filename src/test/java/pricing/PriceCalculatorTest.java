package pricing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PriceCalculatorTest {
    private final PriceCalculator priceCalculator = new PriceCalculator();

    @ParameterizedTest
    @CsvSource({
            "0, 500.00",
            "510, 500.00",
            "1000, 970.00",
            "10000, 9700.00",
            "10000.01, 9200.01",
            "12000, 11040.00"
    })
    void calculateCommercialPrice_loyalClient_returnsExpectedPrice(String baseCost, String expectedPrice) {
        // Given
        ServiceOrder order = createOrderWithCost(baseCost);

        // When
        BigDecimal actualPrice = priceCalculator.calculateCommercialPrice(order);

        // Then
        assertEquals(0, new BigDecimal(expectedPrice).compareTo(actualPrice));
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

    private ServiceOrder createOrderWithCost(String cost) {
        Client client = new Client(1L, "Test domain.Client");
        client.recordCompletedRepair();
        client.recordCompletedRepair();
        client.recordCompletedRepair();
        Car car = new Car("TEST-VIN", "Test Model", "Test Brand", 2024, client);
        ServiceOrder order = new ServiceOrder(car);
        order.addJob(new Job(1L, "Repair", Specialization.ENGINE_REPAIR,
                new BigDecimal(cost), BigDecimal.ZERO));
        return order;
    }
}
