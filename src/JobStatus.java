public enum JobStatus {
    PENDING("Waiting for approval..."),
    IN_PROGRESS("In Progress..."),
    APPROVED("Approved!"),
    COMPLETED("Completed!");

    private final String jobStatusName;

    JobStatus(String jobStatusName) {
        this.jobStatusName = jobStatusName;
    }

    public String getJobStatusName() {
        return jobStatusName;
    }
}