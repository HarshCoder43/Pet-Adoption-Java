import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ShelterGUI extends JFrame {
    private final ShelterManager manager;
    private final DefaultTableModel animalModel;
    private final DefaultTableModel adopterModel;
    private final DefaultTableModel applicationModel;
    private final JLabel statusLabel;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ShelterGUI(ShelterManager manager) {
        this.manager = manager;
        setTitle("Pet Adoption & Shelter Management System");
        setSize(1050, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        animalModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Type", "Breed", "Age", "Behavior", "Intake Date", "Status"}, 0);
        adopterModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Preferred Type", "Max Age", "Experienced"}, 0);
        applicationModel = new DefaultTableModel(
                new Object[]{"Application", "Adopter", "Animal", "Date", "Decision"}, 0);

        tabs.addTab("Animals", createAnimalPanel());
        tabs.addTab("Adopters", createAdopterPanel());
        tabs.addTab("Applications", createApplicationPanel());
        tabs.addTab("Reports", createReportPanel());

        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));

        add(tabs, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        refreshAllTables();
    }

    private JPanel createAnimalPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JTable table = new JTable(animalModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton add = new JButton("Add Animal");
        JButton update = new JButton("Update Animal");
        JButton delete = new JButton("Delete Animal");
        JButton search = new JButton("Search Type");
        JButton sortAge = new JButton("Sort by Age");
        JButton medical = new JButton("Add Medical Record");

        add.addActionListener(e -> addAnimalDialog());
        update.addActionListener(e -> updateAnimalDialog(table));
        delete.addActionListener(e -> deleteAnimalDialog(table));
        search.addActionListener(e -> searchAnimalDialog());
        sortAge.addActionListener(e -> showAnimals("Animals sorted by age", manager.sortAnimalsByAge()));
        medical.addActionListener(e -> addMedicalDialog());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JButton b : new JButton[]{add, update, delete, search, sortAge, medical}) buttons.add(b);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAdopterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JTable table = new JTable(adopterModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton add = new JButton("Add Adopter");
        JButton search = new JButton("Search Adopter");
        JButton match = new JButton("Find Matches");
        add.addActionListener(e -> addAdopterDialog());
        search.addActionListener(e -> searchAdopterDialog());
        match.addActionListener(e -> findMatchDialog());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(add); buttons.add(search); buttons.add(match);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createApplicationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JTable table = new JTable(applicationModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton submit = new JButton("Submit Application");
        JButton approve = new JButton("Approve");
        JButton reject = new JButton("Reject");
        submit.addActionListener(e -> submitApplicationDialog());
        approve.addActionListener(e -> applicationDecisionDialog(true));
        reject.addActionListener(e -> applicationDecisionDialog(false));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(submit); buttons.add(approve); buttons.add(reject);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        JButton refresh = new JButton("Refresh Report");
        refresh.addActionListener(e -> reportArea.setText(buildReport()));
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        panel.add(refresh, BorderLayout.SOUTH);
        reportArea.setText(buildReport());
        return panel;
    }

    private void addAnimalDialog() {
        JTextField id = new JTextField();
        JTextField name = new JTextField();
        JTextField type = new JTextField("Dog");
        JTextField breed = new JTextField("Indie");
        JTextField age = new JTextField("2");
        JTextField behavior = new JTextField("Friendly");
        JTextField intake = new JTextField(LocalDate.now().toString());
        Object[] fields = {"ID", id, "Name", name, "Type", type, "Breed", breed, "Age", age,
                "Behavior", behavior, "Intake Date (yyyy-MM-dd)", intake};
        if (JOptionPane.showConfirmDialog(this, fields, "Add Animal", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                manager.addAnimal(new Animal(Integer.parseInt(id.getText().trim()), name.getText().trim(), type.getText().trim(),
                        breed.getText().trim(), Integer.parseInt(age.getText().trim()), behavior.getText().trim(), parseDate(intake.getText())));
                refreshAllTables();
                statusLabel.setText("Animal added successfully.");
            } catch (Exception ex) { showError(ex); }
        }
    }

    private void updateAnimalDialog(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { showMessage("Select an animal row first."); return; }
        int id = (int) table.getValueAt(row, 0);
        Animal a = manager.getAnimal(id);
        JTextField name = new JTextField(a.getName());
        JTextField type = new JTextField(a.getType());
        JTextField breed = new JTextField(a.getBreed());
        JTextField age = new JTextField(String.valueOf(a.getAge()));
        JTextField behavior = new JTextField(a.getBehavior());
        Object[] fields = {"Name", name, "Type", type, "Breed", breed, "Age", age, "Behavior", behavior};
        if (JOptionPane.showConfirmDialog(this, fields, "Update Animal #" + id, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                manager.updateAnimal(id, name.getText().trim(), type.getText().trim(), breed.getText().trim(),
                        Integer.parseInt(age.getText().trim()), behavior.getText().trim());
                refreshAllTables();
            } catch (Exception ex) { showError(ex); }
        }
    }

    private void deleteAnimalDialog(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { showMessage("Select an animal row first."); return; }
        int id = (int) table.getValueAt(row, 0);
        try {
            manager.deleteAnimal(id);
            refreshAllTables();
            statusLabel.setText("Animal deleted.");
        } catch (Exception ex) { showError(ex); }
    }

    private void addAdopterDialog() {
        JTextField id = new JTextField();
        JTextField name = new JTextField();
        JTextField type = new JTextField("ANY");
        JTextField maxAge = new JTextField("10");
        JCheckBox experienced = new JCheckBox("Has pet experience");
        Object[] fields = {"ID", id, "Name", name, "Preferred Type", type, "Preferred Max Age", maxAge, experienced};
        if (JOptionPane.showConfirmDialog(this, fields, "Add Adopter", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                manager.addAdopter(new Adopter(Integer.parseInt(id.getText().trim()), name.getText().trim(), type.getText().trim(),
                        Integer.parseInt(maxAge.getText().trim()), experienced.isSelected()));
                refreshAllTables();
            } catch (Exception ex) { showError(ex); }
        }
    }

    private void submitApplicationDialog() {
        JTextField appId = new JTextField();
        JTextField adopterId = new JTextField();
        JTextField animalId = new JTextField();
        Object[] fields = {"Application ID", appId, "Adopter ID", adopterId, "Animal ID", animalId};
        if (JOptionPane.showConfirmDialog(this, fields, "Submit Application", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                manager.submitApplication(new Application(Integer.parseInt(appId.getText().trim()), Integer.parseInt(adopterId.getText().trim()),
                        Integer.parseInt(animalId.getText().trim()), LocalDate.now()));
                refreshAllTables();
            } catch (Exception ex) { showError(ex); }
        }
    }

    private void applicationDecisionDialog(boolean approve) {
        String value = JOptionPane.showInputDialog(this, "Enter Application ID:");
        if (value == null) return;
        try {
            int id = Integer.parseInt(value.trim());
            if (approve) manager.approveApplication(id); else manager.rejectApplication(id);
            refreshAllTables();
        } catch (Exception ex) { showError(ex); }
    }

    private void addMedicalDialog() {
        JTextField animalId = new JTextField();
        JTextField date = new JTextField(LocalDate.now().toString());
        JTextField diagnosis = new JTextField("Routine Check");
        JTextField treatment = new JTextField("General Care");
        JTextField vet = new JTextField("Dr. Sharma");
        Object[] fields = {"Animal ID", animalId, "Date", date, "Diagnosis", diagnosis, "Treatment", treatment, "Veterinarian", vet};
        if (JOptionPane.showConfirmDialog(this, fields, "Medical Record", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                manager.addMedicalRecord(Integer.parseInt(animalId.getText().trim()),
                        new MedicalRecord(parseDate(date.getText()), diagnosis.getText().trim(), treatment.getText().trim(), vet.getText().trim()));
                statusLabel.setText("Medical record added.");
            } catch (Exception ex) { showError(ex); }
        }
    }

    private void searchAnimalDialog() {
        String type = JOptionPane.showInputDialog(this, "Enter animal type (e.g., Dog):");
        if (type != null && !type.isBlank()) showAnimals("Search results", manager.searchAnimalsByType(type.trim()));
    }

    private void searchAdopterDialog() {
        String name = JOptionPane.showInputDialog(this, "Enter adopter name:");
        if (name == null || name.isBlank()) return;
        List<Adopter> results = manager.searchAdoptersByName(name.trim());
        StringBuilder sb = new StringBuilder();
        for (Adopter a : results) sb.append(a).append('\n');
        showMessage(results.isEmpty() ? "No adopter found." : sb.toString());
    }

    private void findMatchDialog() {
        String value = JOptionPane.showInputDialog(this, "Enter Adopter ID:");
        if (value == null) return;
        try {
            List<Animal> matches = manager.findMatches(Integer.parseInt(value.trim()));
            showAnimals("Suitable Animals", matches);
        } catch (Exception ex) { showError(ex); }
    }

    private void showAnimals(String title, List<Animal> animals) {
        StringBuilder sb = new StringBuilder();
        for (Animal a : animals) sb.append(a).append('\n');
        showMessage(sb.length() == 0 ? "No records found." : sb.toString());
        statusLabel.setText(title + ": " + animals.size() + " record(s)");
    }

    private String buildReport() {
        var report = manager.getStatusReport();
        return "SHELTER REPORT\n\n"
                + "Total Animals: " + manager.getAnimals().size() + "\n"
                + "Available: " + report.get(AnimalStatus.AVAILABLE) + "\n"
                + "Pending: " + report.get(AnimalStatus.PENDING) + "\n"
                + "Adopted: " + report.get(AnimalStatus.ADOPTED) + "\n\n"
                + "Total Adopters: " + manager.getAdopters().size() + "\n"
                + "Total Applications: " + manager.getApplications().size() + "\n";
    }

    private void refreshAllTables() {
        animalModel.setRowCount(0);
        for (Animal a : manager.getAnimals()) {
            animalModel.addRow(new Object[]{a.getId(), a.getName(), a.getType(), a.getBreed(), a.getAge(),
                    a.getBehavior(), a.getIntakeDate(), a.getStatus()});
        }
        adopterModel.setRowCount(0);
        for (Adopter a : manager.getAdopters()) {
            adopterModel.addRow(new Object[]{a.getId(), a.getName(), a.getPreferredType(), a.getPreferredMaxAge(), a.isExperiencedWithPets()});
        }
        applicationModel.setRowCount(0);
        for (Application a : manager.getApplications()) {
            applicationModel.addRow(new Object[]{a.getApplicationId(), a.getAdopterId(), a.getAnimalId(), a.getApplicationDate(), a.getDecision()});
        }
    }

    private LocalDate parseDate(String value) {
        try { return LocalDate.parse(value.trim(), dateFormatter); }
        catch (DateTimeParseException ex) { throw new IllegalArgumentException("Date must be yyyy-MM-dd."); }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
