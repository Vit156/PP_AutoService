package com.autoservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "cars")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "car_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("CAR")

public class Car {
    private static final int FIRST_CAR_YEAR = 1886;

    @Id
    @Column(name = "vin_code", nullable = false, unique = true, length = 50)
    private String vinCode;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(name = "release_year", nullable = false)
    private int year;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private Client owner;

    protected Car() {
    }

    public Car(String vinCode, String model, String brand, int year, Client owner) {
        if (vinCode == null || vinCode.isBlank()) {
            throw new IllegalArgumentException("VIN code cannot be null or empty");
        }
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("domain.Car model cannot be null or empty");
        }
        if (brand == null || brand.isBlank()) {
            throw new IllegalArgumentException("domain.Car brand cannot be null or empty");
        }
        if (owner == null) {
            throw new IllegalArgumentException("domain.Car owner cannot be null");
        }
        if (year < FIRST_CAR_YEAR || year > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("Invalid car year");
        }

        this.vinCode = vinCode;
        this.model = model;
        this.brand = brand;
        this.year = year;
        this.owner = owner;
    }

    public String getVinCode() {
        return vinCode;
    }

    public void setVinCode(String vinCode) {
        this.vinCode = vinCode;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Client getOwner() {
        return owner;
    }

    public void setOwner(Client owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Car car)) {
            return false;
        }
        return Objects.equals(vinCode, car.vinCode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(vinCode);
    }
}