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

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpecialization(List<Specialization> specialization) {
        this.specialization = specialization;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Specialization> getSpecialization() {
        return specialization;
    }

    public boolean getAvailable() {
        return isAvailable;
    }

    public boolean hasSpecialization(Specialization specialization) {
        return this.specialization.contains(specialization);
    }



}
