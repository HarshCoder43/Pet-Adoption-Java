import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        ShelterManager manager = new ShelterManager();
        seedDemoData(manager);

        // Launch the required Swing GUI.
        javax.swing.SwingUtilities.invokeLater(() -> new ShelterGUI(manager).setVisible(true));
    }

    private static void seedDemoData(ShelterManager manager) {
        Animal a1 = new Animal(101, "Bruno", "Dog", "Indie", 3, "Friendly", LocalDate.of(2026, 9, 10));
        Animal a2 = new Animal(102, "Milo", "Cat", "Persian", 2, "Calm", LocalDate.of(2026, 9, 12));
        Animal a3 = new Animal(103, "Luna", "Dog", "Labrador", 5, "Playful", LocalDate.of(2026, 9, 15));
        manager.addAnimal(a1);
        manager.addAnimal(a2);
        manager.addAnimal(a3);

        manager.addAdopter(new Adopter(201, "Aarav", "Dog", 5, true));
        manager.addAdopter(new Adopter(202, "Diya", "Cat", 8, false));

        manager.addMedicalRecord(101, new MedicalRecord(LocalDate.of(2026, 9, 11), "Vaccination", "Rabies vaccine", "Dr. Mehta"));
        manager.addMedicalRecord(102, new MedicalRecord(LocalDate.of(2026, 9, 13), "Check-up", "Routine examination", "Dr. Shah"));

        // An example application is kept commented so all demo animals stay visible as AVAILABLE.
        // manager.submitApplication(new Application(301, 201, 101, LocalDate.now()));
    }
}
