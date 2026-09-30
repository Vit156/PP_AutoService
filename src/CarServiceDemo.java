import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

public class CarServiceDemo {
    public static void main(String[] args) {
        System.out.println("=== Start of Car Service Demo ===\n");

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
        System.out.println("Success: Client " + client1.getName() + ", car " + car1.getBrand () + " " + car1.getModel () + " (VIN: " + car1.getVinCode() + "), and mechanics are registered.\n");

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
        System.out.println("--- Scenario 9: Mechanic cannot perform work outside their specialization ---");
        ServiceOrder specializationOrder = new ServiceOrder(car1);
        specializationOrder.diagnoseOrder();
        specializationOrder.addJob(new Job(6L, "Brakes repair", Specialization.BRAKE_SYSTEM_REPAIR, new BigDecimal("800"), BigDecimal.ZERO));
        try {
            specializationOrder.assignMechanic(engineMechanic); // Не той механік
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            System.out.println();
        }

        // ---------------------------------------------------------
        // Add multiple company taxis to one service order
        // ---------------------------------------------------------
        System.out.println("--- Scenario 10: Add multiple taxis to a service order ---");
        Car car2 = new Car("VIN987654321", "Camry", "Toyota", 2022, client1);
        Car car3 = new Car("VIN456789123", "Civic", "Honda", 2023, client1);
        service.registerCar(car2);
        service.registerCar(car3);

        TaxiCompany<Car> taxiCompany = new TaxiCompany<>();
        taxiCompany.addTaxi(car1);
        taxiCompany.addTaxi(car2);
        taxiCompany.addTaxi(car3);

        ServiceOrder fleetOrder = new ServiceOrder(car1);
        taxiCompany.addTaxisToOrder(fleetOrder, Arrays.asList(car2, car3));
        service.registerServiceOrder(fleetOrder);
        System.out.println("Cars in the fleet order: " + fleetOrder.getCars().size());
        for (Car car : fleetOrder.getCars()) {
            System.out.println(car.getBrand() + " " + car.getModel() + " (VIN: " + car.getVinCode() + ")");
        }
    }
}








