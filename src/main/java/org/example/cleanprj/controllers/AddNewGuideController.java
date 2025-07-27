package org.example.cleanprj.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.cleanprj.dao.GuideDAO;
import org.example.cleanprj.models.Guide;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddNewGuideController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField languagesField;
    @FXML private TextField experienceField;
    @FXML private TextField contactField;
    @FXML private ComboBox<String> specializationComboBox;
    @FXML private Button closeButton;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    private GuideDAO guideDAO;

    public AddNewGuideController() {
        this.guideDAO = new GuideDAO();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        specializationComboBox.getItems().addAll(
                "Trekking",
                "Heritage Tours",
                "City Sightseeing",
                "Mountain Climbing",
                "Cultural Tours",
                "Adventure Sports",
                "Wildlife Safari"
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
    private void handleSave() {
        if (!validateForm()) {
            return;
        }

        try {
            String name = nameField.getText().trim();
            String languages = languagesField.getText().trim();
            int experience = Integer.parseInt(experienceField.getText().trim());
            String contact = contactField.getText().trim();
            String specialization = specializationComboBox.getValue();

            Guide guide = new Guide(name, languages, experience, contact, specialization);
            guideDAO.save(guide);

            showAlert("Success", "Guide added successfully!");
            closeDialog();

        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Experience must be a valid number!");
        } catch (IOException e) {
            showAlert("Error", "Failed to save guide: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Name is required!");
            return false;
        }

        if (languagesField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Languages are required!");
            return false;
        }

        if (experienceField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Experience is required!");
            return false;
        }

        if (contactField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Contact is required!");
            return false;
        }

        if (specializationComboBox.getValue() == null) {
            showAlert("Validation Error", "Specialization is required!");
            return false;
        }

        try {
            int exp = Integer.parseInt(experienceField.getText().trim());
            if (exp < 0) {
                showAlert("Validation Error", "Experience cannot be negative!");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Experience must be a valid number!");
            return false;
        }

        return true;
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
