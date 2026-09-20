public class Car {
    private String vinCode;
    private String carModel;
    private String carBrand;
    private int carYear;
    private Client carOwner;

    public Car(String vinCode, String carModel, String carBrand, int carYear, Client carOwner) {
        if (vinCode == null || vinCode.trim().isEmpty()) {
            throw new IllegalArgumentException("VIN code cannot be null or empty");
        }
        if (carModel == null || carModel.trim().isEmpty()) {
            throw new IllegalArgumentException("Car model cannot be null or empty");
        }
        if (carBrand == null || carBrand.trim().isEmpty()) {
            throw new IllegalArgumentException("Car brand cannot be null or empty");
        }
        if (carYear < 1886 || carYear > 2023) {
            throw new IllegalArgumentException("Invalid car year");
        }
        if (carOwner == null) {
            throw new IllegalArgumentException("Car owner cannot be null");
        }

        this.vinCode = vinCode;
        this.carModel = carModel;
        this.carBrand = carBrand;
        this.carYear = carYear;
        this.carOwner = carOwner;
    }

    public Client getCarOwner() {
        return carOwner;
    }

    public String getVinCode() {
        return vinCode;
    }

    public String getCarModel() {
        return carModel;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public int getCarYear() {
        return carYear;
    }
}
