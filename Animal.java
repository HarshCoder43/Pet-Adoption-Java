import java.time.LocalDate;
import java.util.LinkedList;

public class Animal implements Adoptable {
    private final int id;
    private String name;
    private String type;
    private String breed;
    private int age;
    private String behavior;
    private final LocalDate intakeDate;
    private AnimalStatus status;
    private final LinkedList<MedicalRecord> medicalHistory;

    public Animal(int id, String name, String type, String breed, int age,
                  String behavior, LocalDate intakeDate) {
        if (id <= 0) throw new IllegalArgumentException("Animal ID must be positive.");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Animal name is required.");
        if (type == null || type.isBlank()) throw new IllegalArgumentException("Animal type is required.");
        if (breed == null || breed.isBlank()) throw new IllegalArgumentException("Breed is required.");
        if (age < 0) throw new IllegalArgumentException("Age cannot be negative.");
        if (behavior == null || behavior.isBlank()) throw new IllegalArgumentException("Behavior is required.");
        if (intakeDate == null || intakeDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid intake date.");
        }
        this.id = id;
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.age = age;
        this.behavior = behavior;
        this.intakeDate = intakeDate;
        this.status = AnimalStatus.AVAILABLE;
        this.medicalHistory = new LinkedList<>();
    }

    @Override
    public boolean isEligibleForAdoption(Adopter adopter) {
        return status == AnimalStatus.AVAILABLE && adopter != null && adopter.isEligible();
    }

    public void addMedicalRecord(MedicalRecord record) {
        if (record == null) throw new IllegalArgumentException("Medical record cannot be null.");
        medicalHistory.add(record);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getBreed() { return breed; }
    public int getAge() { return age; }
    public String getBehavior() { return behavior; }
    public LocalDate getIntakeDate() { return intakeDate; }
    public AnimalStatus getStatus() { return status; }
    public LinkedList<MedicalRecord> getMedicalHistory() { return medicalHistory; }

    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setBreed(String breed) { this.breed = breed; }
    public void setAge(int age) { this.age = age; }
    public void setBehavior(String behavior) { this.behavior = behavior; }
    public void setStatus(AnimalStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "ID=" + id + ", Name=" + name + ", Type=" + type + ", Breed=" + breed
                + ", Age=" + age + ", Behavior=" + behavior + ", Intake=" + intakeDate
                + ", Status=" + status;
    }
}
