import java.math.BigDecimal;

public class Job {
    private int jobId;
    private String jobName;
    private JobStatus jobStatus;
    private BigDecimal jobCost;
    private BigDecimal detailCost;

    public Job(int jobId, String jobName, BigDecimal jobCost, BigDecimal detailCost) {
        if (jobId <= 0) {
            throw new IllegalArgumentException("Job ID must be a positive integer");
        }

        if (jobName == null || jobName.trim().isEmpty()) {
            throw new IllegalArgumentException("Job name cannot be null or empty");
        }

        if (jobCost == null || jobCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Job cost cannot be null or negative");
        }

        if (detailCost == null || detailCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Detail cost cannot be null or negative");
        }

        this.jobId = jobId;
        this.jobName = jobName;
        this.jobStatus = JobStatus.PENDING;
        this.jobCost = jobCost;
        this.detailCost = detailCost;
    }

    public void markCompleted() {
        this.jobStatus = JobStatus.COMPLETED;
    }

    public boolean isCompleted() {
        return this.jobStatus == JobStatus.COMPLETED;
    }

    public BigDecimal getTotalCost() {
        return jobCost.add(detailCost);
    }

//    public int getJobId() {
//        return jobId;
//    }
//
//    public String getJobName() {
//        return jobName;
//    }
//
//    public JobStatus getJobStatus() {
//        return jobStatus;
//    }
//
//    public BigDecimal getJobCost() {
//        return jobCost;
//    }
//
//    public BigDecimal getDetailCost() {
//        return detailCost;
//    }
}
