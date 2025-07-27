package org.example.cleanprj.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.cleanprj.dao.GuideDAO;
import org.example.cleanprj.models.Booking;
import org.example.cleanprj.models.Guide;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class GuideSelectionController implements Initializable {

    @FXML private Label bookingInfoLabel;
    @FXML private TableView<Guide> guidesTable;
    @FXML private TableColumn<Guide, String> guideNameColumn;
    @FXML private TableColumn<Guide, String> guideLanguagesColumn;
    @FXML private TableColumn<Guide, String> guideSpecializationColumn;
    @FXML private TableColumn<Guide, String> guideExperienceColumn;
    @FXML private TableColumn<Guide, String> guideRatingColumn;
    @FXML private TableColumn<Guide, String> guideContactColumn;
    @FXML private Button assignButton;
    @FXML private Button cancelButton;

    private Booking booking;
    private AdminController adminController;
    private GuideDAO guideDAO;
    private ObservableList<Guide> availableGuides = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        guideDAO = new GuideDAO();
        initializeTable();
        loadAvailableGuides();

        // Enable assign button only when a guide is selected
        assignButton.setDisable(true);
        guidesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            assignButton.setDisable(newSelection == null);
        });
    }

    private void initializeTable() {
        guideNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        guideLanguagesColumn.setCellValueFactory(new PropertyValueFactory<>("languages"));
        guideSpecializationColumn.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        guideExperienceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExperience() + " years"));
        guideRatingColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.1f", cellData.getValue().getRating())));
        guideContactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));

        // Set selection mode to single
        guidesTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void loadAvailableGuides() {
        try {
            List<Guide> allGuides = guideDAO.findAll();
            availableGuides.clear();

            // Only show active guides
            for (Guide guide : allGuides) {
                if ("Active".equals(guide.getStatus())) {
                    availableGuides.add(guide);
                }
            }

            guidesTable.setItems(availableGuides);
        } catch (IOException e) {
            showAlert("Error", "Failed to load guides: " + e.getMessage());
        }
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
        updateBookingInfo();
    }

    public void setAdminController(AdminController adminController) {
        this.adminController = adminController;
    }

    private void updateBookingInfo() {
        if (booking != null) {
            String info = String.format(
                    "Tourist: %s | Attraction: %s | Dates: %s - %s | Cost: $%.2f | Status: %s",
                    booking.getTouristName(),
                    booking.getAttractionName(),
                    booking.getStartDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    booking.getEndDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    booking.getTotalCost(),
                    booking.getStatus()
            );
            bookingInfoLabel.setText(info);
        }
    }

    @FXML
    private void handleAssign() {
        Guide selectedGuide = guidesTable.getSelectionModel().getSelectedItem();

        if (selectedGuide == null) {
            showAlert("Warning", "Please select a guide to assign.");
            return;
        }

        // Confirm assignment
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Assignment");
        confirmAlert.setHeaderText("Assign Guide");
        confirmAlert.setContentText(String.format(
                "Are you sure you want to assign guide '%s' to this booking?\n\n" +
                        "Tourist: %s\nAttraction: %s\nGuide: %s (%s)",
                selectedGuide.getName(),
                booking.getTouristName(),
                booking.getAttractionName(),
                selectedGuide.getName(),
                selectedGuide.getContact()
        ));

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // Notify admin controller about the assignment
            if (adminController != null) {
                adminController.onGuideAssigned(booking, selectedGuide);
            }

            // Close dialog
            handleCancel();
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
