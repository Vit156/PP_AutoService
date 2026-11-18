package com.autoservice.service;

import com.autoservice.domain.Car;
import com.autoservice.domain.Client;
import com.autoservice.domain.Job;
import com.autoservice.domain.Mechanic;
import com.autoservice.domain.OrderStatus;
import com.autoservice.domain.ServiceOrder;
import com.autoservice.domain.Specialization;
import com.autoservice.repository.CarRepository;
import com.autoservice.repository.ClientRepository;
import com.autoservice.repository.MechanicRepository;
import com.autoservice.repository.RepairOrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class RepairOrderServiceIntegrationTest {

    @Autowired
    private RepairOrderService repairOrderService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private MechanicRepository mechanicRepository;

    @Autowired
    private RepairOrderRepository repairOrderRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void saveAndLoadOrder_persistsInPostgreSQL() {
        // Given
        Client client = clientRepository.save(new Client("Integration Test Client"));
        Car car = carRepository.save(new Car("VIN-" + UUID.randomUUID(), "Accord", "Honda", 2021, client));

        ServiceOrder order = new ServiceOrder(car);
        order.diagnoseOrder();
        order.addJob(new Job("Oil Change", Specialization.ENGINE_REPAIR, new BigDecimal("600"), new BigDecimal("900")));

        // When
        ServiceOrder savedOrder = repairOrderService.saveOrder(order);
        assertNotNull(savedOrder.getId(), "PostgreSQL must generate order ID");

        ServiceOrder loadedOrder = repairOrderService.getOrderById(savedOrder.getId());

        // Then
        assertEquals(savedOrder.getId(), loadedOrder.getId());
        assertEquals(OrderStatus.DIAGNOSED, loadedOrder.getStatus());
        assertEquals(1, loadedOrder.getJobs().size());
        assertEquals("Oil Change", loadedOrder.getJobs().get(0).getName());

        // Cleanup
        repairOrderRepository.deleteById(savedOrder.getId());
        carRepository.deleteById(car.getVinCode());
        clientRepository.deleteById(client.getId());
    }

    @Test
    void completeServiceOrder_updatesStatusPriceAndClientRepairsTransactionally() {
        // Given
        Client client = clientRepository.save(new Client("Completion Test Client"));
        Car car = carRepository.save(new Car("VIN-" + UUID.randomUUID(), "Camry", "Toyota", 2022, client));
        Mechanic mechanic = mechanicRepository.save(
                new Mechanic("Test Mechanic", Collections.singletonList(Specialization.ENGINE_REPAIR))
        );

        ServiceOrder order = new ServiceOrder(car);
        order.diagnoseOrder();
        Job job = new Job("Timing Belt Replacement", Specialization.ENGINE_REPAIR, new BigDecimal("1000"), new BigDecimal("500"));
        order.addJob(job);
        order.approveOrder();
        order.assignMechanic(mechanic);
        order.startProgress();
        job.markCompleted();

        ServiceOrder savedOrder = repairOrderService.saveOrder(order);
        Long orderId = savedOrder.getId();

        // When
        BigDecimal commercialPrice = repairOrderService.completeServiceOrder(orderId);

        // Then
        ServiceOrder completedOrder = repairOrderService.getOrderById(orderId);
        Client updatedClient = clientRepository.findById(client.getId()).orElseThrow();

        assertEquals(OrderStatus.COMPLETED, completedOrder.getStatus());
        assertNotNull(completedOrder.getCompletedAt());
        assertEquals(new BigDecimal("1500.00"), commercialPrice);
        assertEquals(new BigDecimal("1500.00"), completedOrder.getFinalPrice());
        assertEquals(1L, updatedClient.getCompletedRepairs());

        // Cleanup
        repairOrderRepository.deleteById(orderId);
        mechanicRepository.deleteById(mechanic.getId());
        carRepository.deleteById(car.getVinCode());
        clientRepository.deleteById(client.getId());
    }

    @Test
    void addJobsBatch_whenOneJobIsNull_rollsBackEntireBatch() {
        // Given
        Client client = clientRepository.save(new Client("Rollback Test Client"));
        Car car = carRepository.save(new Car("VIN-" + UUID.randomUUID(), "Civic", "Honda", 2023, client));

        ServiceOrder order = new ServiceOrder(car);
        order.diagnoseOrder();
        ServiceOrder savedOrder = repairOrderService.saveOrder(order);
        Long orderId = savedOrder.getId();

        Job validJob = new Job("Diagnostics", Specialization.ENGINE_REPAIR, new BigDecimal("400"), BigDecimal.ZERO);

        // When
        assertThrows(
                IllegalArgumentException.class,
                () -> repairOrderService.addJobsBatch(orderId, Arrays.asList(validJob, null))
        );

        // Then
        ServiceOrder reloadedOrder = repairOrderService.getOrderById(orderId);
        assertEquals(0, reloadedOrder.getJobs().size(), "None of the jobs from the failed batch should be persisted");

        // Cleanup
        repairOrderRepository.deleteById(orderId);
        carRepository.deleteById(car.getVinCode());
        clientRepository.deleteById(client.getId());
    }

    @Test
    void concurrentUpdate_whenTwoTransactionsModifySameOrder_throwsOptimisticLockingException() throws Exception {
        // Given
        Client client = clientRepository.save(new Client("Optimistic Lock Client"));
        Car car = carRepository.save(new Car("VIN-" + UUID.randomUUID(), "Model 3", "Tesla", 2023, client));
        ServiceOrder savedOrder = repairOrderService.saveOrder(new ServiceOrder(car));
        Long orderId = savedOrder.getId();
        assertEquals(0L, savedOrder.getVersion());

        CountDownLatch bothLoaded = new CountDownLatch(2);
        CountDownLatch tx1Committed = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            // When: TX-1 reads version 0 and commits first (version -> 1)
            Future<?> tx1 = executor.submit(() -> transactionTemplate.executeWithoutResult(status -> {
                ServiceOrder order1 = repairOrderRepository.findById(orderId).orElseThrow();
                bothLoaded.countDown();
                try {
                    bothLoaded.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                order1.diagnoseOrder();
                repairOrderRepository.saveAndFlush(order1);
            }));

            // TX-2 also reads version 0, waits until TX-1 commits, then tries to update stale version 0
            Future<?> tx2 = executor.submit(() -> transactionTemplate.executeWithoutResult(status -> {
                ServiceOrder order2 = repairOrderRepository.findById(orderId).orElseThrow();
                bothLoaded.countDown();
                try {
                    bothLoaded.await();
                    tx1Committed.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                order2.cancelOrder();
                repairOrderRepository.saveAndFlush(order2);
            }));

            tx1.get();
            tx1Committed.countDown();

            ExecutionException exception = assertThrows(
                    ExecutionException.class,
                    tx2::get
            );

            // Then
            assertEquals(
                    ObjectOptimisticLockingFailureException.class,
                    exception.getCause().getClass()
            );

            ServiceOrder reloaded = repairOrderService.getOrderById(orderId);
            assertEquals(OrderStatus.DIAGNOSED, reloaded.getStatus());
            assertEquals(1L, reloaded.getVersion());
        } finally {
            executor.shutdown();
            repairOrderRepository.deleteById(orderId);
            carRepository.deleteById(car.getVinCode());
            clientRepository.deleteById(client.getId());
        }
    }
}