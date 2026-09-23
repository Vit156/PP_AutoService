import java.math.BigDecimal;

public class Job {
    private Long id;
    private String name;
    private JobStatus status;
    private Specialization specialization;
    private BigDecimal workCost;
    private BigDecimal partsCost;

    public Job(Long id, String jobName, BigDecimal workCost, BigDecimal partsCost) {
        if (id.compareTo(0L) <= 0) {
            throw new IllegalArgumentException("Job ID must be a positive integer");
        }

        if (jobName == null || jobName.isBlank()) {
            throw new IllegalArgumentException("Job name cannot be null or empty");
        }

        if (workCost == null || workCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Job cost cannot be null or negative");
        }

        if (partsCost == null || partsCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Detail cost cannot be null or negative");
        }

        this.id = id;
        this.name = jobName;
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

    public String getJobName() {
        return name;
    }

    public JobStatus getJobStatus() {
        return status;
    }

    public BigDecimal getWorkCost() {
        return workCost;
    }

    public BigDecimal getPartsCost() {
        return partsCost;
    }
}
