import java.time.LocalDate;

public class Car {
    private String vinCode;
    private String model;
    private String brand;
    private int year;
    private Client owner;

    public Car(String vinCode, String model, String brand, int year, Client owner) {
        if (vinCode == null || vinCode.isBlank()) {
            throw new IllegalArgumentException("VIN code cannot be null or empty");
        }
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Car model cannot be null or empty");
        }
        if (brand == null || brand.isBlank()) {
            throw new IllegalArgumentException("Car brand cannot be null or empty");
        }
        if (owner == null) {
            throw new IllegalArgumentException("Car owner cannot be null");
        }
        if (year < 1886 || year > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("Invalid car year");
        }


        this.vinCode = vinCode;
        this.model = model;
        this.brand = brand;
        this.year = year;
        this.owner = owner;
    }

    public void setVinCode(String vinCode) {
        this.vinCode = vinCode;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setOwner(Client owner) {
        this.owner = owner;
    }

    public String getVinCode() {
        return vinCode;
    }

    public String getModel() {
        return model;
    }

    public String getBrand() {
        return brand;
    }

    public int getYear() {
        return year;
    }

    public Client getOwner() {
        return owner;
    }
}
