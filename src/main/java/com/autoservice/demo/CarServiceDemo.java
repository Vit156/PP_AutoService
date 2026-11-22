package com.autoservice.demo;

import com.autoservice.domain.*;
import com.autoservice.repository.CarRepository;
import com.autoservice.repository.ClientRepository;
import com.autoservice.repository.MechanicRepository;
import com.autoservice.repository.RepairOrderRepository;
import com.autoservice.service.AutoService;
import com.autoservice.service.RepairOrderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class CarServiceDemo implements CommandLineRunner {

    private final RepairOrderService repairOrderService;
    private final ClientRepository clientRepository;
    private final CarRepository carRepository;
    private final MechanicRepository mechanicRepository;
    private final RepairOrderRepository repairOrderRepository;
    private final TransactionTemplate transactionTemplate;

    public CarServiceDemo(RepairOrderService repairOrderService, RepairOrderRepository repairOrderRepository,
                          ClientRepository clientRepository, CarRepository carRepository,
                          MechanicRepository mechanicRepository, TransactionTemplate transactionTemplate) {
        this.repairOrderService = repairOrderService;
        this.repairOrderRepository = repairOrderRepository;
        this.clientRepository = clientRepository;
        this.carRepository = carRepository;
        this.mechanicRepository = mechanicRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Start of Car Service Demo ===\n");

        AutoService service = new AutoService();

        // ---------------------------------------------------------
        // 1. Успішна реєстрація клієнта та автомобіля
        // ---------------------------------------------------------
        System.out.println("--- Scenario 1: Successful registration of client and car ---");
        Client client1 = new Client(1L, "Vitalik");
        Car car1 = new Car("VIN123456789", "Mustang", "Ford", 2021, client1);
        Mechanic engineMechanic = new Mechanic(1L, "Taras", Collections.singletonList(Specialization.ENGINE_REPAIR));
        Mechanic brakeMechanic = new Mechanic(2L, "Ivan", Collections.singletonList(Specialization.BRAKE_SYSTEM_REPAIR));
        service.registerClient(client1);
        service.registerCar(car1);
        service.registerMechanic(engineMechanic);
        service.registerMechanic(brakeMechanic);
        System.out.println("Success: Client " + client1.getName() + ", car " + car1.getBrand() + " " + car1.getModel() + " (VIN: " + car1.getVinCode() + "), and mechanics are registered.\n");

        // ---------------------------------------------------------
        // 2 & 3. Успішний цикл замовлення з двома роботами
        // ---------------------------------------------------------
        System.out.println("--- Scenario 2 and 3: Successful order cycle with two jobs ---");
        ServiceOrder order1 = new ServiceOrder(car1);
        service.registerServiceOrder(order1);
        System.out.println("Status: " + order1.getStatus().getOrderStatusName());

        order1.diagnoseOrder();
        System.out.println("Status after diagnosis: " + order1.getStatus().getOrderStatusName());

        Job job1 = new Job(1L, "Oil Change", Specialization.ENGINE_REPAIR, new BigDecimal("500"), new BigDecimal("1000"));
        Job job2 = new Job(2L, "Filter Replacement", Specialization.ENGINE_REPAIR, new BigDecimal("300"), new BigDecimal("600"));
        order1.addJob(job1);
        order1.addJob(job2);

        order1.approveOrder();
        System.out.println("Status after approval: " + order1.getStatus().getOrderStatusName());

        order1.assignMechanic(engineMechanic);
        order1.startProgress();
        System.out.println("Status during execution: " + order1.getStatus().getOrderStatusName());

        job1.markCompleted();
        job2.markCompleted();
        order1.completeOrder();

        System.out.println("Status after completion: " + order1.getStatus().getOrderStatusName());
        System.out.println("Success: Total cost of order = " + order1.calculateTotalCost() + " UAH.\n");

        // ---------------------------------------------------------
        // 4. Спроба погодити порожнє замовлення
        // ---------------------------------------------------------
        System.out.println("--- Scenario 4: Attempt to approve an empty order ---");
        ServiceOrder emptyOrder = new ServiceOrder(car1);
        emptyOrder.diagnoseOrder();
        try {
            emptyOrder.approveOrder();
        } catch (IllegalStateException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
        System.out.println();

        // ---------------------------------------------------------
        // 5. Спроба запустити замовлення без механіка
        // ---------------------------------------------------------
        System.out.println("--- Scenario 5: Attempt to start order without mechanic ---");
        ServiceOrder noMechanicOrder = new ServiceOrder(car1);
        noMechanicOrder.diagnoseOrder();
        noMechanicOrder.addJob(new Job(3L, "Test", Specialization.ENGINE_REPAIR, new BigDecimal("100"), BigDecimal.ZERO));
        noMechanicOrder.approveOrder();
        try {
            noMechanicOrder.startProgress();
        } catch (IllegalStateException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
        System.out.println();

        // ---------------------------------------------------------
        // 6. Спроба призначити зайнятого механіка
        // ---------------------------------------------------------
        System.out.println("--- Scenario 6: Attempt to assign a busy mechanic ---");
        ServiceOrder activeOrder = new ServiceOrder(car1);
        activeOrder.diagnoseOrder();
        activeOrder.addJob(new Job(4L, "Repair", Specialization.ENGINE_REPAIR, new BigDecimal("1000"), BigDecimal.ZERO));
        activeOrder.approveOrder();
        activeOrder.assignMechanic(engineMechanic);

        ServiceOrder newOrder = new ServiceOrder(car1);
        newOrder.diagnoseOrder();
        try {
            newOrder.assignMechanic(engineMechanic);
        } catch (IllegalStateException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
        System.out.println();

        // ---------------------------------------------------------
        // 7. Спроба змінити завершене замовлення
        // ---------------------------------------------------------
        System.out.println("--- Scenario 7: Attempt to modify a completed order ---");
        try {
            order1.addJob(new Job(5L, "Late work", Specialization.ENGINE_REPAIR, new BigDecimal("500"), BigDecimal.ZERO));
        } catch (IllegalStateException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
        System.out.println();

        // ---------------------------------------------------------
        // 8. Заборонений перехід між статусами
        // ---------------------------------------------------------
        System.out.println("--- Scenario 8: Illegal state transition ---");
        ServiceOrder illegalStateOrder = new ServiceOrder(car1);
        try {
            illegalStateOrder.completeOrder();
        } catch (IllegalStateException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
        System.out.println();

        // ---------------------------------------------------------
        // 9. Механік не може виконати роботу поза своєю спеціалізацією
        // ---------------------------------------------------------
        System.out.println("--- Scenario 9: Mechanic cannot perform work outside their specialization ---");
        ServiceOrder specializationOrder = new ServiceOrder(car1);
        specializationOrder.diagnoseOrder();
        specializationOrder.addJob(new Job(6L, "Brakes repair", Specialization.BRAKE_SYSTEM_REPAIR, new BigDecimal("800"), BigDecimal.ZERO));
        try {
            specializationOrder.assignMechanic(engineMechanic);
        } catch (IllegalStateException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
        System.out.println();

        // ---------------------------------------------------------
        // 10. Додавання декількох таксі до одного замовлення
        // ---------------------------------------------------------
        System.out.println("--- Scenario 10: Add multiple taxis to a service order ---");
        Taxi taxi1 = new Taxi("TAXI-111", "Camry", "Toyota", 2022, client1, "Uklon");
        Taxi taxi2 = new Taxi("TAXI-222", "Civic", "Honda", 2023, client1, "Bolt");
        Taxi taxi3 = new Taxi("TAXI-333", "Octavia", "Skoda", 2021, client1, "Uber");

        service.registerCar(taxi1);
        service.registerCar(taxi2);
        service.registerCar(taxi3);

        TaxiCompany taxiCompany = new TaxiCompany();
        taxiCompany.addTaxi(taxi1);
        taxiCompany.addTaxi(taxi2);
        taxiCompany.addTaxi(taxi3);

        ServiceOrder fleetOrder = new ServiceOrder(taxi1);
        taxiCompany.addTaxisToOrder(fleetOrder, Arrays.asList(taxi2, taxi3));
        service.registerServiceOrder(fleetOrder);

        System.out.println("Cars in the fleet order: " + fleetOrder.getCars().size());
        fleetOrder.getCars().forEach(car -> {
            if (car instanceof Taxi t) {
                System.out.println(t.getBrand() + " " + t.getModel() + " (VIN: " + t.getVinCode() + ", Company: " + t.getCompanyName() + ")");
            } else {
                System.out.println(car.getBrand() + " " + car.getModel() + " (VIN: " + car.getVinCode() + ")");
            }
        });
        System.out.println();

        // ---------------------------------------------------------
        // 11. Перевірка завантаження даних з PostgreSQL після попередніх запусків
        // ---------------------------------------------------------
        System.out.println("--- Scenario 11: Loading persisted orders from PostgreSQL ---");
        List<ServiceOrder> existingOrders = repairOrderService.getAllOrders();
        System.out.println("Previously saved orders in DB: " + existingOrders.size());

        existingOrders.forEach(o -> {
            String jobsInfo = o.getJobs().stream()
                    .map(j -> j.getName() + " [" + j.getStatus() + "]")
                    .toList()
                    .toString();
            System.out.println("Loaded Order #" + o.getId() + " | Status: " + o.getStatus() + " | Jobs: " + jobsInfo);
        });
        System.out.println();

        // ---------------------------------------------------------
        // 12. Збереження нового замовлення в PostgreSQL та транзакційне завершення
        // ---------------------------------------------------------
        System.out.println("--- Scenario 12: PostgreSQL Persistence & Transactional Completion ---");

        Client dbClient = clientRepository.save(new Client("Vitalik (DB)"));
        Car dbCar = carRepository.save(new Car("VIN-DB-999", "Mustang", "Ford", 2021, dbClient));
        Mechanic dbMechanic = mechanicRepository.save(new Mechanic("Petro", Collections.singletonList(Specialization.ENGINE_REPAIR)));

        ServiceOrder dbOrder = new ServiceOrder(dbCar);
        dbOrder.diagnoseOrder();

        Job overhaulJob = new Job("Major Engine Overhaul", Specialization.ENGINE_REPAIR, new BigDecimal("8000"), new BigDecimal("4000"));
        dbOrder.addJob(overhaulJob);
        dbOrder.approveOrder();
        dbOrder.assignMechanic(dbMechanic);
        dbOrder.startProgress();
        overhaulJob.markCompleted();

        ServiceOrder savedOpenOrder = repairOrderService.saveOrder(dbOrder);
        Long generatedOrderId = savedOpenOrder.getId();
        System.out.println("Order #" + generatedOrderId + " saved to PostgreSQL. Base cost: " + savedOpenOrder.calculateTotalCost() + " UAH");

        BigDecimal commercialPrice = repairOrderService.completeServiceOrder(generatedOrderId);

        ServiceOrder completedOrder = repairOrderService.getOrderById(generatedOrderId);
        System.out.println("Order status in PostgreSQL after completion: " + completedOrder.getStatus().getOrderStatusName());
        System.out.println("Commercial price saved in DB: " + completedOrder.getFinalPrice() + " UAH\n");

        // ---------------------------------------------------------
        // 13. Обробка помилки при зверненні до неіснуючого замовлення через сервіс
        // ---------------------------------------------------------
        System.out.println("--- Scenario 13: Attempt to complete non-existent order via RepairOrderService ---");
        try {
            repairOrderService.completeServiceOrder(999999L);
        } catch (IllegalArgumentException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
        System.out.println();

        // ---------------------------------------------------------
        // 14. Пакетне додавання робіт із відкатом транзакції (@Transactional)
        // ---------------------------------------------------------
        System.out.println("--- Scenario 14: Batch adding jobs with @Transactional rollback ---");
        ServiceOrder batchOrder = new ServiceOrder(dbCar);
        batchOrder.diagnoseOrder();
        ServiceOrder savedBatchOrder = repairOrderService.saveOrder(batchOrder);
        Long batchOrderId = savedBatchOrder.getId();

        Job validBatchJob = new Job("Wheel Alignment", Specialization.ENGINE_REPAIR, new BigDecimal("700"), BigDecimal.ZERO);
        try {
            repairOrderService.addJobsBatch(batchOrderId, Arrays.asList(validBatchJob, null));
        } catch (IllegalArgumentException e) {
            System.out.println("Expected rollback error: " + e.getMessage());
        }

        ServiceOrder afterRollbackOrder = repairOrderService.getOrderById(batchOrderId);
        System.out.println("Jobs count in DB after failed batch (must be 0): " + afterRollbackOrder.getJobs().size() + "\n");

        // ---------------------------------------------------------
        // 15. Оптимістичне блокування (@Version) при одночасному редагуванні
        // ---------------------------------------------------------
        System.out.println("--- Scenario 15: Optimistic Locking (@Version) conflict ---");
        ServiceOrder concurrentOrder = repairOrderService.saveOrder(new ServiceOrder(dbCar));
        Long lockOrderId = concurrentOrder.getId();
        System.out.println("Created Order #" + lockOrderId + " with initial version = " + concurrentOrder.getVersion());

        CountDownLatch bothLoaded = new CountDownLatch(2);
        CountDownLatch tx1Committed = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            // Транзакція 1: читає замовлення (version = 0), проводить діагностику та комітить (version стає 1)
            Future<?> tx1 = executor.submit(() -> transactionTemplate.executeWithoutResult(status -> {
                ServiceOrder orderInTx1 = repairOrderRepository.findById(lockOrderId).orElseThrow();
                System.out.println("[TX-1] Loaded Order #" + lockOrderId + " with version = " + orderInTx1.getVersion());
                bothLoaded.countDown();
                try {
                    bothLoaded.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                orderInTx1.diagnoseOrder();
                repairOrderRepository.saveAndFlush(orderInTx1);
                System.out.println("[TX-1] Committed diagnosis. New version = " + orderInTx1.getVersion());
            }));

            // Транзакція 2: одночасно читає те саме замовлення (version = 0), чекає коміту TX-1 і намагається скасувати його
            Future<?> tx2 = executor.submit(() -> transactionTemplate.executeWithoutResult(status -> {
                ServiceOrder orderInTx2 = repairOrderRepository.findById(lockOrderId).orElseThrow();
                System.out.println("[TX-2] Loaded Order #" + lockOrderId + " with version = " + orderInTx2.getVersion());
                bothLoaded.countDown();
                try {
                    bothLoaded.await();
                    tx1Committed.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                orderInTx2.cancelOrder();
                repairOrderRepository.saveAndFlush(orderInTx2);
            }));

            tx1.get();
            tx1Committed.countDown();
            tx2.get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ObjectOptimisticLockingFailureException optEx) {
                System.out.println("[TX-2] Expected optimistic lock conflict caught: " + optEx.getClass().getSimpleName());
                System.out.println("Details: " + optEx.getMessage());
            } else {
                throw new RuntimeException(e.getCause());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }

        ServiceOrder finalLockState = repairOrderService.getOrderById(lockOrderId);
        System.out.println("Final Order #" + lockOrderId + " status in DB: " + finalLockState.getStatus()
                + " | version: " + finalLockState.getVersion() + "\n");
    }
}