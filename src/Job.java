import java.math.BigDecimal;

public class Job {
    private Long id;
    private String name;
    private JobStatus status;
    private Specialization specialization;
    private BigDecimal workCost;
    private BigDecimal partsCost;

    public Job(Long id, String name, Specialization specialization, BigDecimal workCost, BigDecimal partsCost) {
        if (id.compareTo(0L) <= 0) {
            throw new IllegalArgumentException("Job ID must be a positive integer");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Job name cannot be null or empty");
        }

        if (specialization == null) {
            throw new IllegalArgumentException("Job specialization cannot be null");
        }

        if (workCost == null || workCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Job cost cannot be null or negative");
        }

        if (partsCost == null || partsCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Parts cost cannot be null or negative");
        }

        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.status = JobStatus.PENDING;
        this.workCost = workCost;
        this.partsCost = partsCost;
    }

    public void markCompleted() {
        this.status = JobStatus.COMPLETED;
    }

    public boolean isCompleted() {
        return this.status == JobStatus.COMPLETED;
    }

    public BigDecimal getTotalCost() {
        return workCost.add(partsCost);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public JobStatus getStatus() {
        return status;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public BigDecimal getWorkCost() {
        return workCost;
    }

    public BigDecimal getPartsCost() {
        return partsCost;
    }
}
