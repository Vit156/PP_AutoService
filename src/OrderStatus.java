public enum OrderStatus {
        CREATED ("Order Created!"),
        DIAGNOSED ("Order Diagnosed!"),
        APPROVED ("Order Approved!"),
        IN_PROGRESS ("Order In Progress!"),
        COMPLETED ("Order Completed!"),
        CANCELLED ("Order Cancelled!");

    private final String orderStatusName;

    OrderStatus(String orderStatusName) {
        this.orderStatusName = orderStatusName;
    }

    public String getOrderStatusName() {
        return orderStatusName;
    }

}

