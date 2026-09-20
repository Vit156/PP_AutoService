public enum Specialization {
    ENGINE_REPAIR("Engine Repair"),
    TRANSMISSION_REPAIR("Transmission Repair"),
    BRAKE_SYSTEM_REPAIR("Brake System Repair"),
    ELECTRICAL_SYSTEM_REPAIR("Electrical System Repair"),
    SUSPENSION_REPAIR("Suspension Repair"),
    AIR_CONDITIONING_REPAIR("Air Conditioning Repair"),
    BODYWORK_REPAIR("Bodywork Repair"),
    EXHAUST_SYSTEM_REPAIR("Exhaust System Repair");

    private final String specializationName;

    Specialization(String specializationName) {
        this.specializationName = specializationName;
    }

    public String getSpecializationName() {
        return specializationName;
    }
}
