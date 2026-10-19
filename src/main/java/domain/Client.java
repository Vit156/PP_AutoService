package domain;

import java.math.BigDecimal;

public class Client {
    private long id;
    private String name;
    private long completedRepairs;

    public Client(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public void recordCompletedRepair() {
        completedRepairs = Math.incrementExact(completedRepairs);
    }

    public BigDecimal getLoyaltyDiscountPercentage() {
        return BigDecimal.valueOf((completedRepairs / 3) * 3);
    }
}

