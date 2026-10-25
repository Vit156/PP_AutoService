package domain;

public class Taxi extends Car {
    private String companyName;

    public Taxi(String vinCode, String model, String brand, int year, Client client, String companyName) {
        super(vinCode, model, brand, year, client);
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}