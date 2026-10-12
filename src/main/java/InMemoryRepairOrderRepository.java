import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepairOrderRepository implements RepairOrderRepository {
    private final Map<Long, ServiceOrder> orders = new HashMap<>();

    @Override
    public void save(ServiceOrder order) {
        if (order == null) {
            throw new IllegalArgumentException("Service order cannot be null");
        }
        if (order.getId() == null) {
            throw new IllegalArgumentException("Service order ID cannot be null");
        }

        orders.put(order.getId(), order);
    }

    @Override
    public Optional<ServiceOrder> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<ServiceOrder> findAll() {
        return new ArrayList<>(orders.values());
    }
}
