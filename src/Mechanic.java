public class Mechanic {
    private int mechanicId;
    private String mechanicName;
    private Specialization mechanicSpecialization;
    private boolean isAvailable;

    public Mechanic(int mechanicId, String mechanicName, Specialization mechanicSpecialization) {
        this.mechanicId = mechanicId;
        this.mechanicName = mechanicName;
        this.mechanicSpecialization = mechanicSpecialization;
        this.isAvailable = true;
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

    public int getMechanicId() {
        return mechanicId;
    }

//    public String getMechanicName() {
//        return mechanicName;
//    }
//
//    public Specialization getMechanicSpecialization() {
//        return mechanicSpecialization;
//    }

}
