public enum JobStatus {
    PENDING("Waiting for approval"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String jobStatusName;

    JobStatus(String jobStatusName) {
        this.jobStatusName = jobStatusName;
    }

    public String getJobStatusName() {
        return jobStatusName;
    }
}



//    CREATED ("Engine Repair"),
//    DIAGNOSED ("Diagnostic"),
//    APPROVED ("Approval"),
//    IN_PROGRESS ("In Progress"),
//    COMPLETED ("Completed"),
//    CANCELLED ("Cancelled");
//
//    private final String jobStatusName;
//
//    JobStatus(String jobStatusName) {
//        this.jobStatusName = jobStatusName;
//    }
//
//    public String getJobStatusName() {
//        return jobStatusName;
//    }