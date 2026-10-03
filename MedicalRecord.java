import java.time.LocalDate;

public class MedicalRecord {
    private final LocalDate date;
    private final String diagnosis;
    private final String treatment;
    private final String veterinarian;

    public MedicalRecord(LocalDate date, String diagnosis, String treatment, String veterinarian) {
        if (date == null) throw new IllegalArgumentException("Medical date cannot be null.");
        if (diagnosis == null || diagnosis.isBlank()) throw new IllegalArgumentException("Diagnosis is required.");
        if (treatment == null || treatment.isBlank()) throw new IllegalArgumentException("Treatment is required.");
        if (veterinarian == null || veterinarian.isBlank()) throw new IllegalArgumentException("Veterinarian is required.");
        if (date.isAfter(LocalDate.now())) throw new IllegalArgumentException("Medical date cannot be in the future.");
        this.date = date;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.veterinarian = veterinarian;
    }

    public LocalDate getDate() { return date; }
    public String getDiagnosis() { return diagnosis; }
    public String getTreatment() { return treatment; }
    public String getVeterinarian() { return veterinarian; }

    @Override
    public String toString() {
        return date + " | " + diagnosis + " | " + treatment + " | Vet: " + veterinarian;
    }
}
