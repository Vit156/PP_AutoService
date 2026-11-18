package com.autoservice.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "mechanics")
public class Mechanic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mechanic_specializations", joinColumns = @JoinColumn(name = "mechanic_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "specialization", nullable = false, length = 50)
    private List<Specialization> specialization = new ArrayList<>();

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;

    protected Mechanic() {
    }

    public Mechanic(String name, List<Specialization> specialization) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Mechanic name cannot be null or empty");
        }
        if (specialization == null || specialization.isEmpty()) {
            throw new IllegalArgumentException("Mechanic specializations cannot be null or empty");
        }
        this.name = name;
        this.specialization = new ArrayList<>(specialization);
        this.isAvailable = true;
    }

    public Mechanic(Long id, String name, List<Specialization> specialization) {
        this(name, specialization);
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Mechanic ID must be a positive integer");
        }
        this.id = id;
    }

    public void assignToTask() {
        isAvailable = false;
    }

    public void releaseFromTask() {
        isAvailable = true;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean hasSpecialization(Specialization targetSpecialization) {
        return targetSpecialization != null && this.specialization.contains(targetSpecialization);
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
            throw new IllegalArgumentException("Mechanic name cannot be null or empty");
        }
        this.name = name;
    }

    public List<Specialization> getSpecialization() {
        return Collections.unmodifiableList(specialization);
    }

    public void setSpecialization(List<Specialization> specialization) {
        if (specialization == null || specialization.isEmpty()) {
            throw new IllegalArgumentException("Mechanic specializations cannot be null or empty");
        }
        this.specialization = new ArrayList<>(specialization);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Mechanic mechanic)) {
            return false;
        }
        return id != null && Objects.equals(id, mechanic.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}