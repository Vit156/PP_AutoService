package repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import domain.Car;
import domain.Client;
import domain.ServiceOrder;
import org.junit.jupiter.api.Test;

class InMemoryRepairOrderRepositoryTest {

    private final InMemoryRepairOrderRepository repository = new InMemoryRepairOrderRepository();

    @Test
    void save_validOrder_savesSuccessfully() {
        // Given
        ServiceOrder order = createOrder(1L);

        // When
        repository.save(order);

        // Then
        assertEquals(order, repository.findById(1L).orElseThrow());
    }

    @Test
    void save_nullOrder_throwsIllegalArgumentException() {
        // Given
        ServiceOrder order = null;

        // When
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.save(order)
        );

        // Then
        assertEquals("Service order cannot be null", exception.getMessage());
    }

    @Test
    void save_nullId_throwsIllegalArgumentException() {
        // Given
        ServiceOrder order = createOrder(null);

        // When
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.save(order)
        );

        // Then
        assertEquals("Service order ID cannot be null", exception.getMessage());
    }

    @Test
    void save_existingId_updatesOrder() {
        // Given
        ServiceOrder originalOrder = createOrder(1L);
        ServiceOrder updatedOrder = createOrder(1L);
        repository.save(originalOrder);

        // When
        repository.save(updatedOrder);

        // Then
        assertEquals(updatedOrder, repository.findById(1L).orElseThrow());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void findById_existingId_returnsOrder() {
        // Given
        ServiceOrder order = createOrder(1L);
        repository.save(order);

        // When
        var result = repository.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(order, result.orElseThrow());
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        // Given
        Long nonExistingId = 99L;

        // When
        var result = repository.findById(nonExistingId);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findAll_returnsAllSavedOrders() {
        // Given
        ServiceOrder firstOrder = createOrder(1L);
        ServiceOrder secondOrder = createOrder(2L);
        repository.save(firstOrder);
        repository.save(secondOrder);

        // When
        List<ServiceOrder> result = repository.findAll();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.containsAll(List.of(firstOrder, secondOrder)));
    }

    private ServiceOrder createOrder(Long id) {
        Client client = new Client(1L, "Test domain.Client");
        Car car = new Car("TEST-VIN", "Test Model", "Test Brand", 2024, client);
        ServiceOrder order = new ServiceOrder(car);
        order.setId(id);
        return order;
    }
}
