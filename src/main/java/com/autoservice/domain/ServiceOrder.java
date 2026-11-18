package com.autoservice.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import jakarta.persistence.Version;


@Entity
@Table(name = "service_orders")
public class ServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @JoinTable(
            name = "service_order_cars",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "car_vin_code")
    )
    private List<Car> cars = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_mechanic_id")
    private Mechanic assignedMechanic;

    @OneToMany(mappedBy = "serviceOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Job> jobs = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false, length = 50)
    private OrderStatus currentStatus;

    @Column(name = "final_price", precision = 19, scale = 2)
    private BigDecimal finalPrice;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    @Version
    @Column(name = "version", nullable = false)
    private Long version;
    protected ServiceOrder() {

    }


    public ServiceOrder(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("domain.Car cannot be null");
        }

        this.cars = new ArrayList<>();
        this.cars.add(car);
        this.jobs = new ArrayList<>();
        this.currentStatus = OrderStatus.CREATED;
    }

    public void addCars(List<Car> carsToAdd) {
        if (carsToAdd == null || carsToAdd.isEmpty()) {
            throw new IllegalArgumentException("Cars cannot be null or empty");
        }
        if (this.currentStatus != OrderStatus.CREATED && this.currentStatus != OrderStatus.DIAGNOSED) {
            throw new IllegalStateException("Cannot add cars to order with status: " + this.currentStatus.getOrderStatusName());
        }

        Set<String> existingVins = this.cars.stream()
                .map(Car::getVinCode)
                .collect(Collectors.toSet());

        carsToAdd.forEach(car -> {
            if (car == null) {
                throw new IllegalArgumentException("domain.Car cannot be null");
            }
            if (!existingVins.add(car.getVinCode())) {
                throw new IllegalStateException("domain.Car already belongs to this order");
            }
        });

        this.cars.addAll(carsToAdd);
    }

    public void addJob(Job job) {
        if (job == null) {
            throw new IllegalArgumentException("domain.Job cannot be null");
        }
        if (this.currentStatus != OrderStatus.CREATED && this.currentStatus != OrderStatus.DIAGNOSED) {
            throw new IllegalStateException("Cannot add job to order with status: " + this.currentStatus.getOrderStatusName());
        }

        job.setServiceOrder(this);
        this.jobs.add(job);
    }

    public void assignMechanic(Mechanic mechanic) {
        if (mechanic == null) {
            throw new IllegalArgumentException("domain.Mechanic cannot be null");
        }

        this.jobs.stream()
                .filter(job -> !mechanic.hasSpecialization(job.getSpecialization()))
                .findFirst()
                .ifPresent(job -> {
                    throw new IllegalStateException("domain.Mechanic lacks required specialization for job: " + job.getName());
                });

        if (!mechanic.isAvailable()) {
            throw new IllegalStateException("domain.Mechanic is not available");
        }

        if (this.currentStatus != OrderStatus.CREATED
                && this.currentStatus != OrderStatus.DIAGNOSED
                && this.currentStatus != OrderStatus.APPROVED) {
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
        boolean hasIncompleteJobs = jobs.stream().anyMatch(job -> !job.isCompleted());
        if (hasIncompleteJobs) {
            throw new IllegalStateException("Cannot complete order with incomplete jobs");
        }

        this.currentStatus = OrderStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        getClient().recordCompletedRepair();
        if (this.assignedMechanic != null) {
            this.assignedMechanic.releaseFromTask();
        }
    }

    public void cancelOrder() {
        if (this.currentStatus == OrderStatus.IN_PROGRESS
                || this.currentStatus == OrderStatus.COMPLETED
                || this.currentStatus == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Unable to cancel order with status: " + this.currentStatus.getOrderStatusName());
        }
        this.currentStatus = OrderStatus.CANCELLED;
    }

    public BigDecimal calculateTotalCost() {
        return jobs.stream()
                .map(Job::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Car getCar() {
        if (cars.isEmpty()) {
            throw new IllegalStateException("Service order has no associated cars");
        }
        return cars.get(0);
    }

    public Client getClient() {
        return getCar().getOwner();
    }

    public List<Car> getCars() {
        return Collections.unmodifiableList(cars);
    }

    public Mechanic getAssignedMechanic() {
        return assignedMechanic;
    }

    public List<Job> getJobs() {
        return Collections.unmodifiableList(jobs);
    }

    public OrderStatus getStatus() {
        return currentStatus;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceOrder that)) {
            return false;
        }
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}