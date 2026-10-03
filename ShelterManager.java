import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ShelterManager {
    // Required collections from the case study
    private final ArrayList<Animal> animals = new ArrayList<>();
    private final ArrayList<Adopter> adopters = new ArrayList<>();
    private final ArrayList<Application> applications = new ArrayList<>();
    private final HashMap<Integer, Animal> animalMap = new HashMap<>();
    private final TreeMap<LocalDate, List<Animal>> animalsByIntakeDate = new TreeMap<>();
    private final LinkedHashMap<Integer, Adopter> adopterMap = new LinkedHashMap<>();

    // ---------- Animal CRUD ----------
    public void addAnimal(Animal animal) {
        if (animal == null) throw new IllegalArgumentException("Animal cannot be null.");
        if (animalMap.containsKey(animal.getId())) throw new IllegalArgumentException("Animal ID already exists.");
        animals.add(animal);
        animalMap.put(animal.getId(), animal);
        animalsByIntakeDate.computeIfAbsent(animal.getIntakeDate(), d -> new ArrayList<>()).add(animal);
    }

    public void updateAnimal(int id, String name, String type, String breed, int age, String behavior) {
        Animal a = getAnimal(id);
        if (name == null || name.isBlank() || type == null || type.isBlank()
                || breed == null || breed.isBlank() || behavior == null || behavior.isBlank() || age < 0) {
            throw new IllegalArgumentException("Invalid animal update data.");
        }
        a.setName(name);
        a.setType(type);
        a.setBreed(breed);
        a.setAge(age);
        a.setBehavior(behavior);
    }

    public void deleteAnimal(int id) {
        Animal animal = getAnimal(id);
        if (animal.getStatus() != AnimalStatus.AVAILABLE) {
            throw new IllegalStateException("Only available animals can be deleted.");
        }
        animals.remove(animal);
        animalMap.remove(id);
        List<Animal> list = animalsByIntakeDate.get(animal.getIntakeDate());
        if (list != null) {
            list.remove(animal);
            if (list.isEmpty()) animalsByIntakeDate.remove(animal.getIntakeDate());
        }
    }

    public Animal getAnimal(int id) {
        Animal animal = animalMap.get(id);
        if (animal == null) throw new IllegalArgumentException("Animal not found.");
        return animal;
    }

    public List<Animal> getAnimals() { return new ArrayList<>(animals); }

    // ---------- Adopter ----------
    public void addAdopter(Adopter adopter) {
        if (adopter == null) throw new IllegalArgumentException("Adopter cannot be null.");
        if (adopterMap.containsKey(adopter.getId())) throw new IllegalArgumentException("Adopter ID already exists.");
        adopters.add(adopter);
        adopterMap.put(adopter.getId(), adopter);
    }

    public List<Adopter> getAdopters() { return new ArrayList<>(adopters); }

    public Adopter getAdopter(int id) {
        Adopter adopter = adopterMap.get(id);
        if (adopter == null) throw new IllegalArgumentException("Adopter not found.");
        return adopter;
    }

    // ---------- Applications ----------
    public void submitApplication(Application application) {
        if (application == null) throw new IllegalArgumentException("Application cannot be null.");
        if (applications.stream().anyMatch(a -> a.getApplicationId() == application.getApplicationId())) {
            throw new IllegalArgumentException("Application ID already exists.");
        }
        getAdopter(application.getAdopterId());
        Animal animal = getAnimal(application.getAnimalId());
        if (animal.getStatus() != AnimalStatus.AVAILABLE) {
            throw new IllegalStateException("Animal is not currently available.");
        }
        applications.add(application);
        animal.setStatus(AnimalStatus.PENDING);
    }

    public void approveApplication(int applicationId) {
        Application app = getApplication(applicationId);
        if (!"PENDING".equals(app.getDecision())) throw new IllegalStateException("Application already decided.");
        Adopter adopter = getAdopter(app.getAdopterId());
        Animal animal = getAnimal(app.getAnimalId());

        if (!animal.isEligibleForAdoption(adopter) && animal.getStatus() != AnimalStatus.PENDING) {
            throw new IllegalStateException("Animal is not eligible for adoption.");
        }
        if (!adopter.matchesPreferences(animal)) {
            throw new IllegalStateException("Adopter preferences do not match this animal.");
        }
        app.approve();
        animal.setStatus(AnimalStatus.ADOPTED);
    }

    public void rejectApplication(int applicationId) {
        Application app = getApplication(applicationId);
        if (!"PENDING".equals(app.getDecision())) throw new IllegalStateException("Application already decided.");
        app.reject();
        Animal animal = getAnimal(app.getAnimalId());
        animal.setStatus(AnimalStatus.AVAILABLE);
    }

    public Application getApplication(int id) {
        return applications.stream()
                .filter(a -> a.getApplicationId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Application not found."));
    }

    public List<Application> getApplications() { return new ArrayList<>(applications); }

    // ---------- Medical history ----------
    public void addMedicalRecord(int animalId, MedicalRecord record) {
        getAnimal(animalId).addMedicalRecord(record);
    }

    // ---------- Search / Sorting ----------
    public List<Animal> searchAnimalsByType(String type) {
        List<Animal> result = new ArrayList<>();
        for (Animal animal : animals) {
            if (animal.getType().equalsIgnoreCase(type)) result.add(animal);
        }
        return result;
    }

    public List<Animal> searchAnimalsByName(String name) {
        String query = name.toLowerCase();
        List<Animal> result = new ArrayList<>();
        for (Animal animal : animals) {
            if (animal.getName().toLowerCase().contains(query)) result.add(animal);
        }
        return result;
    }

    public List<Adopter> searchAdoptersByName(String name) {
        String query = name.toLowerCase();
        List<Adopter> result = new ArrayList<>();
        for (Adopter adopter : adopters) {
            if (adopter.getName().toLowerCase().contains(query)) result.add(adopter);
        }
        return result;
    }

    public List<Animal> sortAnimalsByAge() {
        List<Animal> copy = getAnimals();
        copy.sort(Comparator.comparingInt(Animal::getAge));
        return copy;
    }

    public List<Animal> sortAnimalsByIntakeDate() {
        List<Animal> copy = getAnimals();
        copy.sort(Comparator.comparing(Animal::getIntakeDate));
        return copy;
    }

    public List<Animal> getAnimalsGroupedByIntakeDate() {
        List<Animal> result = new ArrayList<>();
        for (List<Animal> dayAnimals : animalsByIntakeDate.values()) result.addAll(dayAnimals);
        return result;
    }

    public List<Animal> findMatches(int adopterId) {
        Adopter adopter = getAdopter(adopterId);
        List<Animal> matches = new ArrayList<>();
        for (Animal animal : animals) if (adopter.matches(animal)) matches.add(animal);
        return matches;
    }

    // ---------- Reports ----------
    public Map<AnimalStatus, Long> getStatusReport() {
        Map<AnimalStatus, Long> report = new java.util.EnumMap<>(AnimalStatus.class);
        for (AnimalStatus status : AnimalStatus.values()) report.put(status, 0L);
        for (Animal animal : animals) report.put(animal.getStatus(), report.get(animal.getStatus()) + 1);
        return report;
    }
}
