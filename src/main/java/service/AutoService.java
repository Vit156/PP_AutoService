package service;

import domain.Car;
import domain.Client;
import domain.Mechanic;
import domain.ServiceOrder;

import java.util.ArrayList;
import java.util.List;

public class AutoService {
    private List<Client> clients;
    private List<Car> cars;
    private List<Mechanic> mechanics;
    private List<ServiceOrder> orders;

    public AutoService() {
        this.clients = new ArrayList<>();
        this.cars = new ArrayList<>();
        this.mechanics = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    public void registerCar(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("domain.Car cannot be null");
        }
        for (Car registeredCar : this.cars) {
            if (registeredCar.getVinCode().equals(car.getVinCode())) {
                throw new IllegalStateException("domain.Car already registered");
            }
        }

        this.cars.add(car);
    }

    public void registerClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("domain.Client cannot be null");
        }
        for (Client registeredClient : this.clients) {
            if (registeredClient.getId() == client.getId()) {
                throw new IllegalStateException("domain.Client already registered");
            }
        }

        this.clients.add(client);
    }

    public void registerMechanic(Mechanic mechanic) {
        if (mechanic == null) {
            throw new IllegalArgumentException("domain.Mechanic cannot be null");
        }
        for (Mechanic registeredMechanic : this.mechanics) {
            if (registeredMechanic.getId() == mechanic.getId()) {
                throw new IllegalStateException("domain.Mechanic already registered");
            }
        }

        this.mechanics.add(mechanic);
    }

    public void registerServiceOrder(ServiceOrder serviceOrder) {
        if (serviceOrder == null) {
            throw new IllegalArgumentException("Service order cannot be null");
        }
        for (ServiceOrder registeredOrder : this.orders) {
            if (registeredOrder.equals(serviceOrder)) {
                throw new IllegalStateException("Service order already registered");
            }
        }

        this.orders.add(serviceOrder);
    }
}



