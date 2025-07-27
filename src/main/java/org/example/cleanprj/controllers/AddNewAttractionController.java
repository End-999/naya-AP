package org.example.cleanprj.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.cleanprj.dao.AttractionDAO;
import org.example.cleanprj.models.Attraction;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddNewAttractionController implements Initializable {

    @FXML private TextField nameField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField locationField;
    @FXML private ComboBox<String> difficultyComboBox;
    @FXML private TextField altitudeField;
    @FXML private TextField durationField;
    @FXML private TextField bestSeasonField;
    @FXML private TextArea descriptionArea;
    @FXML private Button closeButton;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    private AttractionDAO attractionDAO;

    public AddNewAttractionController() {
        this.attractionDAO = new AttractionDAO();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeComboBox.getItems().addAll(
                "Mountain Peak",
                "Trekking Route",
                "Cultural Site",
                "Natural Wonder",
                "Adventure Activity",
                "Historical Monument",
                "Religious Site",
                "National Park"
        );

        difficultyComboBox.getItems().addAll(
                "Easy",
                "Moderate",
                "Challenging",
                "Difficult",
                "Extreme"
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
            String type = typeComboBox.getValue();
            String location = locationField.getText().trim();
            String difficulty = difficultyComboBox.getValue();
            String altitude = altitudeField.getText().trim();
            String duration = durationField.getText().trim();
            String bestSeason = bestSeasonField.getText().trim();
            String description = descriptionArea.getText().trim();

            // Default price calculation based on difficulty
            double price = calculateDefaultPrice(difficulty);

            Attraction attraction = new Attraction(name, type, location, difficulty,
                    altitude, duration, bestSeason, description, price);
            attractionDAO.save(attraction);

            showAlert("Success", "Attraction added successfully!");
            closeDialog();

        } catch (IOException e) {
            showAlert("Error", "Failed to save attraction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private double calculateDefaultPrice(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "easy": return 500.0;
            case "moderate": return 800.0;
            case "challenging": return 1200.0;
            case "difficult": return 1800.0;
            case "extreme": return 2500.0;
            default: return 1000.0;
        }
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Name is required!");
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

        if (difficultyComboBox.getValue() == null) {
            showAlert("Validation Error", "Difficulty is required!");
            return false;
        }

        if (altitudeField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Altitude is required!");
            return false;
        }

        if (durationField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Duration is required!");
            return false;
        }

        if (bestSeasonField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Best season is required!");
            return false;
        }

        if (descriptionArea.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Description is required!");
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
