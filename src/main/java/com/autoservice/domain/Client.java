package com.autoservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "clients")
public class Client {
    private static final long REPAIRS_PER_DISCOUNT_STEP = 3L;
    private static final long DISCOUNT_PERCENT_PER_STEP = 3L;
    private static final long MAX_LOYALTY_DISCOUNT_PERCENT = 60L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "completed_repairs", nullable = false)
    private long completedRepairs;

    protected Client() {
    }

    public Client(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Client name cannot be null or empty");
        }
        this.name = name;
        this.completedRepairs = 0L;
    }

    public Client(Long id, String name) {
        this(name);
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Client ID must be a positive integer");
        }
        this.id = id;
    }

    public void recordCompletedRepair() {
        completedRepairs = Math.incrementExact(completedRepairs);
    }

    public BigDecimal getLoyaltyDiscountPercentage() {
        long steps = completedRepairs / REPAIRS_PER_DISCOUNT_STEP;
        long discount = Math.min(steps * DISCOUNT_PERCENT_PER_STEP, MAX_LOYALTY_DISCOUNT_PERCENT);
        return BigDecimal.valueOf(discount);
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
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Client name cannot be null or empty");
        }
        this.name = name;
    }

    public long getCompletedRepairs() {
        return completedRepairs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Client client)) {
            return false;
        }
        return id != null && Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

