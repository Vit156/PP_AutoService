package domain;

import java.util.ArrayList;
import java.util.List;

public class TaxiCompany<T extends Taxi> {
    private List<T> taxis;

    public TaxiCompany() {
        this.taxis = new ArrayList<>();
    }

    public void addTaxi(T taxi) {
        if (taxi == null) {
            throw new IllegalArgumentException("Taxi cannot be null");
        }
        for (T registeredTaxi : this.taxis) {
            if (registeredTaxi.getVinCode().equals(taxi.getVinCode())) {
                throw new IllegalStateException("Taxi already registered");
            }
        }
        this.taxis.add(taxi);
    }

    public void removeTaxi(T taxi) {
        if (taxi == null) {
            throw new IllegalArgumentException("Taxi cannot be null");
        }
        if (!this.taxis.remove(taxi)) {
            throw new IllegalStateException("Taxi not found in the company");
        }
    }

    public void displayTaxis() {
        if (this.taxis.isEmpty()) {
            System.out.println("No taxis registered in the company.");
            return;
        }
        System.out.println("Registered Taxis:");
        for (T taxi : this.taxis) {
            System.out.println("VIN: " + taxi.getVinCode() + ", Company: " + taxi.getCompanyName());
        }
    }

    public void addTaxisToOrder(ServiceOrder order, List<? extends T> taxisToAdd) {
        if (order == null) {
            throw new IllegalArgumentException("Service order cannot be null");
        }
        if (taxisToAdd == null || taxisToAdd.isEmpty()) {
            throw new IllegalArgumentException("Taxi list cannot be null or empty");
        }
        for (T taxi : taxisToAdd) {
            if (taxi == null) {
                throw new IllegalArgumentException("Taxi cannot be null");
            }
            if (!this.taxis.contains(taxi)) {
                throw new IllegalStateException("Taxi with VIN " + taxi.getVinCode() + " does not belong to the company");
            }
        }
        order.addCars(new ArrayList<>(taxisToAdd));
    }
}