package com.autoservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TAXI")
public class Taxi extends Car {

    @Column(name = "company_name")
    private String companyName;

    protected Taxi() {
        super();
    }

    public Taxi(String vinCode, String model, String brand, int year, Client client, String companyName) {
        super(vinCode, model, brand, year, client);
        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Taxi company name cannot be null or empty");
        }
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        if (companyName == null || companyName.isBlank()) {
            throw new IllegalArgumentException("Taxi company name cannot be null or empty");
        }
        this.companyName = companyName;
    }
}