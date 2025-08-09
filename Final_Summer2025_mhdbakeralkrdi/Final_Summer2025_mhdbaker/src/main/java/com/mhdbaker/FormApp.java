package com.mhdbaker;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.time.LocalDate;

public class FormApp extends Application {

    private TextField fullnameField;
    private TextField contactField;
    private ComboBox<String> educationBox;
    private DatePicker datePicker;
    private TextField salaryField;

    private TextField companyNameField;
    private TextField jobTitleField;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Employment Application Form");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Company logo
        Image logoImage = new Image("file:C:/Users/anass/OneDrive/Desktop/logo512.png");
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitWidth(150);
        logoView.setPreserveRatio(true);
        grid.add(logoView, 2, 0, 1, 3);

        // Fullname
        Label fullnameLabel = new Label("Fullname:");
        fullnameField = new TextField();
        fullnameField.setPromptText("Enter full name");
        grid.add(fullnameLabel, 0, 0);
        grid.add(fullnameField, 1, 0);

        // Contact Number
        Label contactLabel = new Label("Contact Number:");
        contactField = new TextField();
        contactField.setPromptText("10 digit number");
        grid.add(contactLabel, 0, 1);
        grid.add(contactField, 1, 1);

        // Highest Education
        Label educationLabel = new Label("Highest Education:");
        educationBox = new ComboBox<>();
        educationBox.getItems().addAll("Masters", "Bachelors", "College Diploma");
        educationBox.setPromptText("Select education");
        grid.add(educationLabel, 0, 2);
        grid.add(educationBox, 1, 2);

        // Date Applied
        Label dateLabel = new Label("Date Applied:");
        datePicker = new DatePicker(LocalDate.now());
        grid.add(dateLabel, 0, 3);
        grid.add(datePicker, 1, 3);

        // Salary
        Label salaryLabel = new Label("Salary:");
        salaryField = new TextField();
        salaryField.setPromptText("e.g., 12345678.50");
        grid.add(salaryLabel, 0, 4);
        grid.add(salaryField, 1, 4);

        // Employment Fields

        // Company Name
        Label companyLabel = new Label("Company Name:");
        companyNameField = new TextField();
        companyNameField.setPromptText("Enter company name");
        grid.add(companyLabel, 0, 5);
        grid.add(companyNameField, 1, 5);

        // Job Title
        Label jobTitleLabel = new Label("Job Title:");
        jobTitleField = new TextField();
        jobTitleField.setPromptText("Enter job title");
        grid.add(jobTitleLabel, 0, 6);
        grid.add(jobTitleField, 1, 6);

        // Start Date
        Label startDateLabel = new Label("Start Date:");
        startDatePicker = new DatePicker();
        grid.add(startDateLabel, 0, 7);
        grid.add(startDatePicker, 1, 7);

        // End Date
        Label endDateLabel = new Label("End Date:");
        endDatePicker = new DatePicker();
        grid.add(endDateLabel, 0, 8);
        grid.add(endDatePicker, 1, 8);

        // Submit button
        Button submitBtn = new Button("Submit");
        grid.add(submitBtn, 1, 9);

        submitBtn.setOnAction(e -> handleSubmit());

        Scene scene = new Scene(grid, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleSubmit() {
        String fullname = fullnameField.getText().trim();
        String contact = contactField.getText().trim();
        String education = educationBox.getValue();
        LocalDate dateApplied = datePicker.getValue();
        String salaryText = salaryField.getText().trim();

        String companyName = companyNameField.getText().trim();
        String jobTitle = jobTitleField.getText().trim();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        // Validation for applicant data
        if (!fullname.matches("[a-zA-Z ]{1,50}")) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Fullname must be only letters (max 50).");
            return;
        }

        if (!contact.matches("\\d{10}")) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Contact number must be exactly 10 digits.");
            return;
        }

        if (education == null || education.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select your highest education.");
            return;
        }

        if (dateApplied == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a valid date.");
            return;
        }

        double salary;
        try {
            salary = Double.parseDouble(salaryText);
            if (!salaryText.matches("\\d{1,8}(\\.\\d{1,2})?")) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Salary must be numeric with up to 8 digits and max 2 decimals.");
            return;
        }

        // Validation for employment data
        if (companyName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter company name.");
            return;
        }

        if (jobTitle.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter job title.");
            return;
        }

        if (startDate == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select start date.");
            return;
        }

        if (endDate == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select end date.");
            return;
        }

        // Convert dates to SQL Date
        java.sql.Date sqlDateApplied = java.sql.Date.valueOf(dateApplied);
        java.sql.Date sqlStartDate = java.sql.Date.valueOf(startDate);
        java.sql.Date sqlEndDate = java.sql.Date.valueOf(endDate);

        // Insert applicant and get generated applicant_id
        int applicantId = DatabaseHelper.insertApplicant(fullname, contact, education, sqlDateApplied, salary);

        if (applicantId != -1) {
            // Insert employment record linked to applicantId
            DatabaseHelper.insertEmployment(applicantId, companyName, jobTitle, sqlStartDate, sqlEndDate);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Applicant and employment data inserted successfully!");

            // Clear form
            fullnameField.clear();
            contactField.clear();
            educationBox.getSelectionModel().clearSelection();
            datePicker.setValue(LocalDate.now());
            salaryField.clear();

            companyNameField.clear();
            jobTitleField.clear();
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to insert applicant data.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
