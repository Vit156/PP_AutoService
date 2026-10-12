import java.util.List;
import java.util.Optional;

public interface RepairOrderRepository {
    void save(ServiceOrder order);

    Optional<ServiceOrder> findById(Long id);

    List<ServiceOrder> findAll();
}
