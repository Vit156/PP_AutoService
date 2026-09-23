import java.util.List;

public class Mechanic {
    private int Id;
    private String Name;
    private List<Specialization> Specialization;
    private boolean isAvailable;

    public Mechanic(int id, String name, List<Specialization> specialization) {
        this.Id = id;
        this.Name = name;
        this.Specialization = specialization;
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
        return Id;
    }

    public String getMechanicName() {
        return Name;
    }

    public List<Specialization> getMechanicSpecialization() {
        return Specialization;
    }

}
