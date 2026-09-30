import java.math.BigDecimal;
import java.util.Collections;

public class CarServiceDemo {
    public static void main(String[] args) {
        System.out.println("=== Start of Car Service Demo ===\n");

        AutoService service = new AutoService();

        // ---------------------------------------------------------
        // Успішна реєстрація клієнта та автомобіля
        // ---------------------------------------------------------

        System.out.println("1 scenario: Successful registration of client and car");
        Client client = new Client(1L, "Vitalik");
        Car car = new Car("VIN123456789", "Mustang", "Ford", 2021, client);
        Mechanic engineMechanic = new Mechanic(1L, "Taras", Collections.singletonList(Specialization.ENGINE_REPAIR));
        Mechanic brakeMechanic = new Mechanic(2L, "Ivan", Collections.singletonList(Specialization.BRAKE_SYSTEM_REPAIR));
        service.registerClient(client);
        service.registerCar(car);
        service.registerMechanic(engineMechanic);
        service.registerMechanic(brakeMechanic);
        System.out.println("Success: Client " + client.getName() + ", car " + car.getBrand () + " " + car.getModel () + " (VIN: " + car.getVinCode() + "), mechanics " + engineMechanic.getName() + " and " + brakeMechanic.getName() + " are registered.\n");

        // ---------------------------------------------------------
        // Успішний цикл замовлення з двома роботами
        // ---------------------------------------------------------

        System.out.println("--- Scenario 2 and 3: Successful order cycle with two jobs ---");
        ServiceOrder order1 = new ServiceOrder(car);
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
        ServiceOrder emptyOrder = new ServiceOrder(car);
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
        ServiceOrder noMechanicOrder = new ServiceOrder(car);
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

        ServiceOrder activeOrder = new ServiceOrder(car);
        activeOrder.diagnoseOrder();
        activeOrder.addJob(new Job(4L, "Repair", Specialization.ENGINE_REPAIR, new BigDecimal("1000"), BigDecimal.ZERO));
        activeOrder.approveOrder();
        activeOrder.assignMechanic(engineMechanic);

        ServiceOrder newOrder = new ServiceOrder(car);
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
        ServiceOrder illegalStateOrder = new ServiceOrder(car);
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
        System.out.println("--- Scenario 9: Mechanic cannot perform work outside their specialization ---");
        ServiceOrder specializationOrder = new ServiceOrder(car);
        specializationOrder.diagnoseOrder();
        specializationOrder.addJob(new Job(6L, "Brakes repair", Specialization.BRAKE_SYSTEM_REPAIR, new BigDecimal("800"), BigDecimal.ZERO));
        try {
            specializationOrder.assignMechanic(engineMechanic); // Не той механік
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            System.out.println();
        }
    }
}









