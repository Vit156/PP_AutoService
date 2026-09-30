import java.util.List;

public class Mechanic {
    private long id;
    private String name;
    private List<Specialization> specialization;
    private boolean isAvailable;

    public Mechanic(long id, String name, List<Specialization> specialization) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
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


    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean hasSpecialization(Specialization specialization) {
        return this.specialization.contains(specialization);
    }

    public List<Specialization> getSpecialization() {
        return specialization;
    }

}
