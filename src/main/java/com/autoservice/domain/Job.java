package com.autoservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private JobStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Specialization specialization;

    @Column(name = "work_cost", nullable = false, precision = 19, scale = 2)
    private BigDecimal workCost;

    @Column(name = "parts_cost", nullable = false, precision = 19, scale = 2)
    private BigDecimal partsCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_order_id")
    private ServiceOrder serviceOrder;

    protected Job() {
    }

    public Job(String name, Specialization specialization, BigDecimal workCost, BigDecimal partsCost) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("domain.Job name cannot be null or empty");
        }
        if (specialization == null) {
            throw new IllegalArgumentException("domain.Job specialization cannot be null");
        }
        if (workCost == null || workCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("domain.Job cost cannot be null or negative");
        }
        if (partsCost == null || partsCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Parts cost cannot be null or negative");
        }

        this.name = name;
        this.specialization = specialization;
        this.status = JobStatus.PENDING;
        this.workCost = workCost;
        this.partsCost = partsCost;
    }

    public Job(Long id, String name, Specialization specialization, BigDecimal workCost, BigDecimal partsCost) {
        this(name, specialization, workCost, partsCost);
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("domain.Job ID must be a positive integer");
        }
        this.id = id;
    }

    public void markCompleted() {
        this.status = JobStatus.COMPLETED;
    }

    public boolean isCompleted() {
        return this.status == JobStatus.COMPLETED;
    }

    public BigDecimal getTotalCost() {
        return workCost.add(partsCost);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public BigDecimal getWorkCost() {
        return workCost;
    }

    public void setWorkCost(BigDecimal workCost) {
        this.workCost = workCost;
    }

    public BigDecimal getPartsCost() {
        return partsCost;
    }

    public void setPartsCost(BigDecimal partsCost) {
        this.partsCost = partsCost;
    }

    public ServiceOrder getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(ServiceOrder serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Job job)) {
            return false;
        }
        return id != null && Objects.equals(id, job.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}