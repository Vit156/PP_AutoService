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
        if (year < 1886 || year > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("Invalid car year");
        }
        if (owner == null) {
            throw new IllegalArgumentException("Car owner cannot be null");
        }

        this.vinCode = vinCode;
        this.model = model;
        this.brand = brand;
        this.year = year;
        this.owner = owner;
    }

    public Client getOwner() {
        return owner;
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
}
