package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pricing.PriceCalculator;
import repository.RepairOrderRepository;

@ExtendWith(MockitoExtension.class)
class RepairOrderServiceTest {

    @Mock
    private RepairOrderRepository repairOrderRepository;

    @Mock
    private PriceCalculator priceCalculator;

    private RepairOrderService repairOrderService;

     @BeforeEach
     public void init(){
         repairOrderService = new RepairOrderService(repairOrderRepository, priceCalculator);
     }

    @Test
    void completeServiceOrder_orderExists_completesAndReturnsPrice() {
        // Given
        Long orderId = 1L;
        ServiceOrder order = createInProgressOrder();
        BigDecimal expectedPrice = new BigDecimal("1000.00");
        when(repairOrderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(priceCalculator.calculateCommercialPrice(order)).thenReturn(expectedPrice);

        // When
        BigDecimal actualPrice = repairOrderService.completeServiceOrder(orderId);

        // Then
        assertEquals(expectedPrice, actualPrice);
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        verify(repairOrderRepository).save(order);
    }

    @Test
    void completeServiceOrder_invalidState_throwsIllegalStateException() {
        // Given
        Long orderId = 1L;
        Client client = new Client(1L, "Test domain.Client");
        Car car = new Car("TEST-VIN", "Test Model", "Test Brand", 2024, client);
        ServiceOrder order = new ServiceOrder(car);
        when(repairOrderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When
        assertThrows(IllegalStateException.class,
                () -> repairOrderService.completeServiceOrder(orderId));

        // Then
        verify(repairOrderRepository, never()).save(any());
        verifyNoInteractions(priceCalculator);
    }

    @Test
    void completeServiceOrder_orderNotFound_throwsException() {
        // Given
        Long orderId = 1L;
        when(repairOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> repairOrderService.completeServiceOrder(orderId));

        // Then
        assertEquals("Service order not found: " + orderId, exception.getMessage());
        verify(repairOrderRepository, never()).save(any());
        verifyNoInteractions(priceCalculator);
    }

    @Test
    void completeServiceOrder_nullOrderId_throwsException() {
        // Given
        Long nullOrderId = null;

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> repairOrderService.completeServiceOrder(nullOrderId));

        // Then
        assertEquals("Order ID cannot be null", exception.getMessage());
        verifyNoInteractions(repairOrderRepository);
        verifyNoInteractions(priceCalculator);
    }

    private ServiceOrder createInProgressOrder() {
        Client client = new Client(1L, "Test domain.Client");
        Car car = new Car("TEST-VIN", "Test Model", "Test Brand", 2024, client);
        ServiceOrder order = new ServiceOrder(car);
        Job job = new Job(1L, "Repair", Specialization.ENGINE_REPAIR,
                BigDecimal.ZERO, BigDecimal.ZERO);
        order.addJob(job);
        order.diagnoseOrder();
        order.approveOrder();
        order.assignMechanic(new Mechanic(1L, "Test domain.Mechanic",
                Collections.singletonList(Specialization.ENGINE_REPAIR)));
        order.startProgress();
        job.markCompleted();
        return order;
    }
}