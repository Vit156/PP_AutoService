import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

public class ServiceOrder {
    private Car car;
    private Mechanic assignedMechanic;
    private List<Job> jobs;
    private OrderStatus currentStatus;

    public ServiceOrder(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("Car cannot be null");
        }
        this.car = car;
        this.jobs = new ArrayList<>();
        this.currentStatus = OrderStatus.CREATED;
    }

    public void addJob(Job job) {
        if (job == null) {
            throw new IllegalArgumentException("Job cannot be null");
        }
        if (this.currentStatus != OrderStatus.CREATED && this.currentStatus != OrderStatus.DIAGNOSED) {
            throw new IllegalStateException("Cannot add job to order with status: " + this.currentStatus.getOrderStatusName());
        }
        this.jobs.add(job);
    }

    public void assignMechanic(Mechanic mechanic) {
        if (mechanic == null) {
            throw new IllegalArgumentException("Mechanic cannot be null");
        }
        if (!mechanic.isAvailable()) {
            throw new IllegalStateException("Mechanic is not available");
        }
        if (this.currentStatus != OrderStatus.CREATED && this.currentStatus != OrderStatus.DIAGNOSED && this.currentStatus != OrderStatus.APPROVED) {
            throw new IllegalStateException("Cannot assign mechanic to order with status: " + this.currentStatus.getOrderStatusName());
        }
        this.assignedMechanic = mechanic;
        this.assignedMechanic.assignToTask();
    }

    public void diagnoseOrder() {
        if (this.currentStatus != OrderStatus.CREATED) {
            throw new IllegalStateException("Unable to diagnose order with status: " + this.currentStatus.getOrderStatusName());
        }
        this.currentStatus = OrderStatus.DIAGNOSED;
    }

    public void approveOrder() {
        if (this.currentStatus != OrderStatus.DIAGNOSED) {
            throw new IllegalStateException("Unable to approve order with status: " + this.currentStatus.getOrderStatusName());
        }
        if (jobs.isEmpty()) {
            throw new IllegalStateException("Cannot approve order without any jobs");
        }
        this.currentStatus = OrderStatus.APPROVED;
    }

    public void startProgress() {
        if (this.currentStatus != OrderStatus.APPROVED || this.assignedMechanic == null) {
            throw new IllegalStateException("Unable to start progress on order without assigned mechanic and with status: " + this.currentStatus.getOrderStatusName());
        }
        this.currentStatus = OrderStatus.IN_PROGRESS;
    }

    public void completeOrder() {
        if (this.currentStatus != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Unable to complete order with status: " + this.currentStatus.getOrderStatusName());
        }
        for (Job job : jobs) {
            if (!job.isCompleted()) {
                throw new IllegalStateException("Cannot complete order with incomplete jobs");
            }
        }
        this.currentStatus = OrderStatus.COMPLETED;
        if (this.assignedMechanic != null) {
            this.assignedMechanic.releaseFromTask();
        }
    }

    public void cancelOrder() {
        if (this.currentStatus == OrderStatus.IN_PROGRESS || this.currentStatus == OrderStatus.COMPLETED || this.currentStatus == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Unable to cancel order with status: " + this.currentStatus.getOrderStatusName());
        }
        this.currentStatus = OrderStatus.CANCELLED;
    }

    public BigDecimal calculateTotalCost() {
        BigDecimal totalCost = BigDecimal.ZERO;
        for (Job job : jobs) {
            totalCost = totalCost.add(job.getTotalCost());
        }
        return totalCost;
    }


    public Car getCar() {
        return car;
    }

    public Mechanic getAssignedMechanic() {
        return assignedMechanic;
    }

    public List<Job> getJobs() {
        return new ArrayList<>(jobs);
    }

    public OrderStatus getStatus() {
        return currentStatus;
    }

}
