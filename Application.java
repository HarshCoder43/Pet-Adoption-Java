import java.time.LocalDate;

public class Application {
    private final int applicationId;
    private final int adopterId;
    private final int animalId;
    private final LocalDate applicationDate;
    private String decision;

    public Application(int applicationId, int adopterId, int animalId, LocalDate applicationDate) {
        if (applicationId <= 0 || adopterId <= 0 || animalId <= 0) {
            throw new IllegalArgumentException("IDs must be positive.");
        }
        if (applicationDate == null || applicationDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid application date.");
        }
        this.applicationId = applicationId;
        this.adopterId = adopterId;
        this.animalId = animalId;
        this.applicationDate = applicationDate;
        this.decision = "PENDING";
    }

    public int getApplicationId() { return applicationId; }
    public int getAdopterId() { return adopterId; }
    public int getAnimalId() { return animalId; }
    public LocalDate getApplicationDate() { return applicationDate; }
    public String getDecision() { return decision; }

    public void approve() { decision = "APPROVED"; }
    public void reject() { decision = "REJECTED"; }

    @Override
    public String toString() {
        return "Application=" + applicationId + ", Adopter=" + adopterId + ", Animal="
                + animalId + ", Date=" + applicationDate + ", Decision=" + decision;
    }
}
