public class Adopter {
    private final int id;
    private final String name;
    private final String preferredType;
    private final int preferredMaxAge;
    private final boolean experiencedWithPets;

    public Adopter(int id, String name, String preferredType, int preferredMaxAge, boolean experiencedWithPets) {
        if (id <= 0) throw new IllegalArgumentException("Adopter ID must be positive.");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Adopter name is required.");
        if (preferredType == null || preferredType.isBlank()) throw new IllegalArgumentException("Preferred animal type is required.");
        if (preferredMaxAge < 0) throw new IllegalArgumentException("Preferred max age cannot be negative.");
        this.id = id;
        this.name = name;
        this.preferredType = preferredType;
        this.preferredMaxAge = preferredMaxAge;
        this.experiencedWithPets = experiencedWithPets;
    }

    public boolean isEligible() {
        return name.length() >= 2;
    }

    public boolean matches(Animal animal) {
        return animal != null && animal.getStatus() == AnimalStatus.AVAILABLE && matchesPreferences(animal);
    }

    public boolean matchesPreferences(Animal animal) {
        if (animal == null) return false;
        boolean typeMatch = preferredType.equalsIgnoreCase("ANY")
                || preferredType.equalsIgnoreCase(animal.getType());
        boolean ageMatch = animal.getAge() <= preferredMaxAge;
        return typeMatch && ageMatch;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPreferredType() { return preferredType; }
    public int getPreferredMaxAge() { return preferredMaxAge; }
    public boolean isExperiencedWithPets() { return experiencedWithPets; }

    @Override
    public String toString() {
        return "ID=" + id + ", Name=" + name + ", PreferredType=" + preferredType
                + ", MaxAge=" + preferredMaxAge + ", Experienced=" + experiencedWithPets;
    }
}
