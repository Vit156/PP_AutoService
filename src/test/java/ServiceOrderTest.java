import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class ServiceOrderTest {

    @Test
    void addJob_validState_addsJobSuccessfully() {
        // Given
        ServiceOrder order = createOrder();
        Job job = createJob();

        // When
        order.addJob(job);

        // Then
        assertAll(
                () -> assertEquals(1, order.getJobs().size()),
                () -> assertEquals(job, order.getJobs().get(0))
        );
    }

    @Test
    void addJob_invalidState_throwsIllegalStateException() {
        // Given
        ServiceOrder order = createInProgressOrder();
        order.getJobs().get(0).markCompleted();
        order.completeOrder();
        Job additionalJob = createJob();

        // When
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> order.addJob(additionalJob)
        );

        // Then
        assertEquals("Cannot add job to order with status: " + OrderStatus.COMPLETED.getOrderStatusName(),
                exception.getMessage());
    }

    @Test
    void addJob_nullJob_throwsIllegalArgumentException() {
        // Given
        ServiceOrder order = createOrder();

        // When
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> order.addJob(null)
        );

        // Then
        assertEquals("Job cannot be null", exception.getMessage());
    }

    @Test
    void assignMechanic_missingSpecialization_throwsIllegalStateException() {
        // Given
        ServiceOrder order = createOrder();
        Job job = createJob();
        order.addJob(job);
        Mechanic mechanic = new Mechanic(2L, "Test Mechanic",
                Collections.singletonList(Specialization.BODYWORK_REPAIR));

        // When
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> order.assignMechanic(mechanic)
        );

        // Then
        assertEquals("Mechanic lacks required specialization for job: " + job.getName(),
                exception.getMessage());
    }

    @Test
    void completeOrder_incompleteJobs_throwsIllegalStateException() {
        // Given
        ServiceOrder order = createInProgressOrder();

        // When
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                order::completeOrder
        );

        // Then
        assertEquals("Cannot complete order with incomplete jobs", exception.getMessage());
    }

    @Test
    void completeOrder_allJobsCompleted_updatesStatusToCompleted() {
        // Given
        ServiceOrder order = createInProgressOrder();
        order.getJobs().get(0).markCompleted();

        // When
        order.completeOrder();

        // Then
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
    }

    private ServiceOrder createOrder() {
        Client client = new Client(1L, "Test Client");
        Car car = new Car("TEST-VIN", "Test Model", "Test Brand", 2024, client);
        return new ServiceOrder(car);
    }

    private ServiceOrder createInProgressOrder() {
        ServiceOrder order = createOrder();
        order.addJob(createJob());
        order.diagnoseOrder();
        order.approveOrder();
        order.assignMechanic(new Mechanic(1L, "Test Mechanic",
                Collections.singletonList(Specialization.ENGINE_REPAIR)));
        order.startProgress();
        return order;
    }

    private Job createJob() {
        return new Job(1L, "Engine Repair", Specialization.ENGINE_REPAIR,
                BigDecimal.ZERO, BigDecimal.ZERO);
    }
}