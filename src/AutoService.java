import java.util.ArrayList;
import java.util.List;

public class AutoService {
    private List<Client> clients;
    private List<Car> cars;
    private List<Mechanic> mechanics;
    private List<ServiceOrder> orders;

    AutoService() {
        this.clients = new ArrayList<>();
        this.cars = new ArrayList<>();
        this.mechanics = new ArrayList<>();
        this.orders = new ArrayList<>();
    }

    public void registerCar(Car car) {
        for (Car c : this.cars) {
            if (c.getVinCode().equals(car.getVinCode())) {
                throw new IllegalStateException("Car already registered");
            }
        }
        if (car == null) {
            throw new IllegalArgumentException("Car cannot be null");
        }
        this.cars.add(car);
    }

    public void registerClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        for (Client cl : this.clients) {
            if (cl.getClientId() == client.getClientId()) {
                throw new IllegalStateException("Client already registered");
            }
        }

        this.clients.add(client);
    }

    public void registerMechanic(Mechanic mechanic) {
        if (mechanic == null) {
            throw new IllegalArgumentException("Mechanic cannot be null");
        }
        for (Mechanic m : this.mechanics) {
            if (m.getMechanicId() == mechanic.getMechanicId()) {
                throw new IllegalStateException("Mechanic already registered");
            }
        }

        this.mechanics.add(mechanic);
    }

    public void registerServiceOrder(ServiceOrder serviceOrder) {
        if (serviceOrder == null) {
            throw new IllegalArgumentException("Service order cannot be null");
        }
        for (ServiceOrder so : this.orders) {
            if (so.equals(serviceOrder)) {
                throw new IllegalStateException("Service order already registered");
            }
        }

        this.orders.add(serviceOrder);
    }
}



