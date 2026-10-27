package demo;

import domain.*;
import pricing.PriceCalculator;
import repository.InMemoryRepairOrderRepository;
import repository.RepairOrderRepository;
import service.AutoService;
import service.RepairOrderService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

public class CarServiceDemo {
    public static void main(String[] args) {
        System.out.println("=== Start of domain.Car Service Demo ===\n");

        AutoService service = new AutoService();

        // ---------------------------------------------------------
        // Успішна реєстрація клієнта та автомобіля
        // ---------------------------------------------------------

        System.out.println("1 scenario: Successful registration of client and car");
        Client client1 = new Client(1L, "Vitalik");
        Car car1 = new Car("VIN123456789", "Mustang", "Ford", 2021, client1);
        Mechanic engineMechanic = new Mechanic(1L, "Taras", Collections.singletonList(Specialization.ENGINE_REPAIR));
        Mechanic brakeMechanic = new Mechanic(2L, "Ivan", Collections.singletonList(Specialization.BRAKE_SYSTEM_REPAIR));
        service.registerClient(client1);
        service.registerCar(car1);
        service.registerMechanic(engineMechanic);
        service.registerMechanic(brakeMechanic);
        System.out.println("Success: domain.Client " + client1.getName() + ", car " + car1.getBrand () + " " + car1.getModel () + " (VIN: " + car1.getVinCode() + "), and mechanics are registered.\n");

        // ---------------------------------------------------------
        // Успішний цикл замовлення з двома роботами
        // ---------------------------------------------------------

        System.out.println("--- Scenario 2 and 3: Successful order cycle with two jobs ---");
        ServiceOrder order1 = new ServiceOrder(car1);
        service.registerServiceOrder(order1);
        System.out.println("Status: " + order1.getStatus().getOrderStatusName());

        order1.diagnoseOrder();
        System.out.println("Status after diagnosis: " + order1.getStatus().getOrderStatusName());

        Job job1 = new Job(1L, "Oil Change", Specialization.ENGINE_REPAIR ,new BigDecimal("500"), new BigDecimal("1000"));
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
        // Спроба погодити порожнє замовлення
        // ---------------------------------------------------------

        System.out.println("--- Scenario 4: Attempt to approve an empty order ---");
        ServiceOrder emptyOrder = new ServiceOrder(car1);
        emptyOrder.diagnoseOrder();
        try {
            emptyOrder.approveOrder(); // Робіт немає, має впасти
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        System.out.println();

        // ---------------------------------------------------------
        // Спроба запустити замовлення без механіка
        // ---------------------------------------------------------
        System.out.println("--- Scenario 5: Attempt to start order without mechanic ---");
        ServiceOrder noMechanicOrder = new ServiceOrder(car1);
        noMechanicOrder.diagnoseOrder();
        noMechanicOrder.addJob(new Job(3L, "Test", Specialization.ENGINE_REPAIR, new BigDecimal("100"), BigDecimal.ZERO));
        noMechanicOrder.approveOrder();
        try {
            noMechanicOrder.startProgress(); // Механіка не призначили, має впасти
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        System.out.println();

        // ---------------------------------------------------------
        // Спроба призначити зайнятого механіка
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
            newOrder.assignMechanic(engineMechanic); // Спроба забрати зайнятого механіка
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        System.out.println();

        // ---------------------------------------------------------
        // Спроба змінити завершене замовлення
        // ---------------------------------------------------------
        System.out.println("--- Scenario 7: Attempt to modify a completed order ---");
        try {
            order1.addJob(new Job(5L, "Late work", Specialization.ENGINE_REPAIR, new BigDecimal("500"), BigDecimal.ZERO));
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        System.out.println();

        // ---------------------------------------------------------
        // Заборонений перехід між статусами
        // ---------------------------------------------------------
        System.out.println("--- Scenario 8: Illegal state transition ---");
        ServiceOrder illegalStateOrder = new ServiceOrder(car1);
        try {
            // в обхід DIAGNOSED, APPROVED, IN_PROGRESS
            illegalStateOrder.completeOrder();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
        System.out.println();

        // ---------------------------------------------------------
        // Механік не може виконати роботу поза своєю спеціалізацією
        // ---------------------------------------------------------
        System.out.println("--- Scenario 9: domain.Mechanic cannot perform work outside their specialization ---");
        ServiceOrder specializationOrder = new ServiceOrder(car1);
        specializationOrder.diagnoseOrder();
        specializationOrder.addJob(new Job(6L, "Brakes repair", Specialization.BRAKE_SYSTEM_REPAIR, new BigDecimal("800"), BigDecimal.ZERO));
        try {
            specializationOrder.assignMechanic(engineMechanic); // Не той механік
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            System.out.println();
        }

//         ---------------------------------------------------------
//         Add multiple company taxis to one service order
//         ---------------------------------------------------------
        System.out.println("--- Scenario 10: Add multiple taxis to a service order ---");
        Taxi taxi1 = new Taxi("TAXI-111", "Camry", "Toyota", 2022, client1, "Uklon");
        Taxi taxi2 = new Taxi("TAXI-222", "Civic", "Honda", 2023, client1, "Bolt");
        Taxi taxi3 = new Taxi("TAXI-333", "Octavia", "Skoda", 2021, client1, "Uber");

        // Реєструємо таксі в сервісі (оскільки Taxi наслідує Car, метод працює коректно)
        service.registerCar(taxi1);
        service.registerCar(taxi2);
        service.registerCar(taxi3);

        TaxiCompany taxiCompany = new TaxiCompany();
        taxiCompany.addTaxi(taxi1);
        taxiCompany.addTaxi(taxi2);
        taxiCompany.addTaxi(taxi3);

        // Створюємо замовлення для першого таксі та додаємо туди інші
        ServiceOrder fleetOrder = new ServiceOrder(taxi1);
        taxiCompany.addTaxisToOrder(fleetOrder, Arrays.asList(taxi2, taxi3));
        service.registerServiceOrder(fleetOrder);

        System.out.println("Cars in the fleet order: " + fleetOrder.getCars().size());
        for (Car car : fleetOrder.getCars()) {
            if (car instanceof Taxi t) {
                System.out.println(t.getBrand() + " " + t.getModel() + " (VIN: " + t.getVinCode() + ", Company: " + t.getCompanyName() + ")");
            } else {
                System.out.println(car.getBrand() + " " + car.getModel() + " (VIN: " + car.getVinCode() + ")");
            }
        }
        System.out.println();

        // ---------------------------------------------------------
        // Scenario 11: Lab 2 Layered Architecture (Repository, pricing.PriceCalculator, service.RepairOrderService)
        // ---------------------------------------------------------
        System.out.println("--- Scenario 11: Lab 2 Layered Architecture (Repository, pricing.PriceCalculator, service.RepairOrderService) ---");

        // Ініціалізація інфраструктурного шару та сервісу
        RepairOrderRepository repository = new InMemoryRepairOrderRepository();
        PriceCalculator priceCalculator = new PriceCalculator();
        RepairOrderService repairOrderService = new RepairOrderService(repository, priceCalculator);

        // Підготовка нового замовлення (сума > 10000 для перевірки знижки)
        ServiceOrder registeredOrder = new ServiceOrder(car1);
        registeredOrder.setId(101L); // Задаємо ID для збереження в репозиторій
        registeredOrder.diagnoseOrder();

        Job overhaulJob = new Job(10L, "Major Engine Overhaul", Specialization.ENGINE_REPAIR, new BigDecimal("8000"), new BigDecimal("4000"));
        registeredOrder.addJob(overhaulJob);
        registeredOrder.approveOrder();

        // Призначаємо вільного механіка (engineMechanic залишився зайнятим у 6-му сценарії)
        Mechanic lab2Mechanic = new Mechanic(3L, "Petro", Collections.singletonList(Specialization.ENGINE_REPAIR));
        service.registerMechanic(lab2Mechanic);
        registeredOrder.assignMechanic(lab2Mechanic);
        registeredOrder.startProgress();

        overhaulJob.markCompleted();

        // Зберігаємо відкрите замовлення в репозиторій
        repairOrderService.saveOrder(registeredOrder);
        System.out.println("Order #" + registeredOrder.getId() + " saved to repository. Base cost: " + registeredOrder.calculateTotalCost() + " UAH");

        // Викликаємо Application Service, який координує завершення, підрахунок ціни та оновлення в репозиторії
        BigDecimal commercialPrice = repairOrderService.completeServiceOrder(101L);

        // Перевіряємо, що в репозиторії статус змінився, а ціна розрахована калькулятором
        ServiceOrder savedOrder = repairOrderService.getOrderById(101L);
        System.out.println("Order status in repository after service execution: " + savedOrder.getStatus().getOrderStatusName());
        System.out.println("Commercial price with discounts (loyalty + high cost): " + commercialPrice + " UAH\n");

        // ---------------------------------------------------------
        // 12. Обробка помилки при зверненні до неіснуючого замовлення через сервіс
        // ---------------------------------------------------------
        System.out.println("--- Scenario 12: Attempt to complete non-existent order via service.RepairOrderService ---");
        try {
            repairOrderService.completeServiceOrder(999L);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

    }
}








