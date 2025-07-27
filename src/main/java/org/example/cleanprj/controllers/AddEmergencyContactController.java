package org.example.cleanprj.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.cleanprj.dao.EmergencyContactDAO;
import org.example.cleanprj.models.EmergencyContact;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class AddEmergencyContactController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField organizationField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField locationField;
    @FXML private CheckBox available24_7CheckBox;
    @FXML private Button closeButton;
    @FXML private Button cancelButton;
    @FXML private Button addContactButton;

    private EmergencyContactDAO emergencyDAO;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );

    public AddEmergencyContactController() {
        this.emergencyDAO = new EmergencyContactDAO();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeComboBox.getItems().addAll(
                "Police",
                "Fire Department",
                "Medical/Hospital",
                "Mountain Rescue",
                "Tourist Police",
                "Embassy/Consulate",
                "Local Authority",
                "Insurance Provider",
                "Transportation",
                "Other Emergency Service"
        );
    }

    @FXML
    private void handleClose() {
        closeDialog();
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    @FXML
    private void handleAddContact() {
        if (!validateForm()) {
            return;
        }

        try {
            String name = nameField.getText().trim();
            String organization = organizationField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String type = typeComboBox.getValue();
            String location = locationField.getText().trim();
            boolean available24_7 = available24_7CheckBox.isSelected();

            EmergencyContact contact = new EmergencyContact(name, organization, phone,
                    email, type, location, available24_7);
            emergencyDAO.save(contact);

            showAlert("Success", "Emergency contact added successfully!");
            closeDialog();

        } catch (IOException e) {
            showAlert("Error", "Failed to save emergency contact: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Name is required!");
            return false;
        }

        if (organizationField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Organization is required!");
            return false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Phone is required!");
            return false;
        }

        if (!isValidPhoneNumber(phoneField.getText().trim())) {
            showAlert("Validation Error", "Please enter a valid phone number!");
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Email is required!");
            return false;
        }

        if (!EMAIL_PATTERN.matcher(emailField.getText().trim()).matches()) {
            showAlert("Validation Error", "Please enter a valid email address!");
            return false;
        }

        if (typeComboBox.getValue() == null) {
            showAlert("Validation Error", "Type is required!");
            return false;
        }

        if (locationField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Location is required!");
            return false;
        }

        return true;
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("^[+]?[-()\\d\\s]{7,20}$");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeDialog() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
