package org.example.cleanprj.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.cleanprj.dao.AttractionDAO;
import org.example.cleanprj.dao.BookingDAO;
import org.example.cleanprj.dao.EmergencyContactDAO;
import org.example.cleanprj.dao.UserDAO;
import org.example.cleanprj.models.Attraction;
import org.example.cleanprj.models.Booking;
import org.example.cleanprj.models.EmergencyContact;
import org.example.cleanprj.models.User;
import org.example.cleanprj.utils.SessionManager;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TourismController implements Initializable {

    // Header elements
    @FXML private Label welcomeLabel;

    // Tab buttons
    @FXML private ToggleButton exploreTab;
    @FXML private ToggleButton myBookingsTab;
    @FXML private ToggleButton profileTab;
    @FXML private ToggleButton emergencyTab;

    // Content areas
    @FXML private VBox exploreScreen;
    @FXML private VBox myBookingsScreen;
    @FXML private VBox profileScreen;
    @FXML private VBox emergencyScreen;

    // Explore screen elements
    @FXML private TilePane destinationCards;

    // My Bookings screen elements
    @FXML private VBox bookingList;

    // Profile screen elements
    @FXML private Label profileName;
    @FXML private Label profileEmail;
    @FXML private Label profileNationality;
    @FXML private Label profileTotalBookings;
    @FXML private TextField emergencyContactField;
    @FXML private Button updateEmergencyButton;

    // DAOs
    private AttractionDAO attractionDAO;
    private BookingDAO bookingDAO;
    private EmergencyContactDAO emergencyContactDAO;
    private UserDAO userDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDAOs();
        setupTabNavigation();
        updateWelcomeMessage();
        loadDestinations();
        loadBookings();

        // Load user profile only if profile elements exist
        if (profileName != null) {
            loadUserProfile();
        }

        // Setup emergency contact update button
        if (updateEmergencyButton != null) {
            updateEmergencyButton.setOnAction(e -> updateEmergencyContact());
        }

        // Show explore screen by default
        showScreen(exploreScreen);
        setActiveTab(exploreTab);
    }

    private void initializeDAOs() {
        attractionDAO = new AttractionDAO();
        bookingDAO = new BookingDAO();
        emergencyContactDAO = new EmergencyContactDAO();
        userDAO = new UserDAO();
    }

    private void updateWelcomeMessage() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null && welcomeLabel != null) {
            // Capitalize first letter of name and nationality
            String firstName = getFirstName(currentUser.getFullName());
            String nationality = capitalizeFirstLetter(currentUser.getNationality());

            String welcomeMessage = "Welcome " + firstName + " (" + nationality + ")";
            welcomeLabel.setText(welcomeMessage);
        }
    }

    private String getFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "User";
        }

        String[] nameParts = fullName.trim().split("\\s+");
        String firstName = nameParts[0];
        return capitalizeFirstLetter(firstName);
    }

    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }

    private void setupTabNavigation() {
        exploreTab.setOnAction(e -> {
            showScreen(exploreScreen);
            setActiveTab(exploreTab);
            loadDestinations();
        });

        myBookingsTab.setOnAction(e -> {
            showScreen(myBookingsScreen);
            setActiveTab(myBookingsTab);
            loadBookings();
        });

        profileTab.setOnAction(e -> {
            showScreen(profileScreen);
            setActiveTab(profileTab);
            loadUserProfile();
        });

        emergencyTab.setOnAction(e -> {
            showScreen(emergencyScreen);
            setActiveTab(emergencyTab);
        });
    }

    private void showScreen(VBox screenToShow) {
        exploreScreen.setVisible(false);
        exploreScreen.setManaged(false);
        myBookingsScreen.setVisible(false);
        myBookingsScreen.setManaged(false);
        profileScreen.setVisible(false);
        profileScreen.setManaged(false);
        emergencyScreen.setVisible(false);
        emergencyScreen.setManaged(false);

        screenToShow.setVisible(true);
        screenToShow.setManaged(true);
    }

    private void setActiveTab(ToggleButton activeTab) {
        ToggleButton[] allTabs = {exploreTab, myBookingsTab, profileTab, emergencyTab};

        for (ToggleButton tab : allTabs) {
            if (tab == activeTab) {
                tab.getStyleClass().remove("inactive-tab");
                if (!tab.getStyleClass().contains("active-tab")) {
                    tab.getStyleClass().add("active-tab");
                }
            } else {
                tab.getStyleClass().remove("active-tab");
                if (!tab.getStyleClass().contains("inactive-tab")) {
                    tab.getStyleClass().add("inactive-tab");
                }
            }
        }
    }

    private void loadDestinations() {
        try {
            List<Attraction> attractions = attractionDAO.findAll();
            destinationCards.getChildren().clear();

            for (Attraction attraction : attractions) {
                VBox card = createDestinationCard(attraction);
                destinationCards.getChildren().add(card);
            }
        } catch (IOException e) {
            System.err.println("Error loading destinations: " + e.getMessage());
            showAlert("Error", "Failed to load destinations: " + e.getMessage());
        }
    }

    private VBox createDestinationCard(Attraction attraction) {
        VBox card = new VBox(10);
        card.getStyleClass().add("attraction-card");
        card.setPrefWidth(350);
        card.setPadding(new Insets(15));

        // Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(320);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        try {
            String imagePath = "/org/example/cleanprj/images/" + getImageForAttraction(attraction.getName());
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            imageView.setImage(image);
        } catch (Exception e) {
            // Use placeholder if image not found
            System.err.println("Image not found for: " + attraction.getName());
        }

        // Title
        Label titleLabel = new Label(attraction.getName());
        titleLabel.getStyleClass().add("attraction-title");

        // Location and duration
        Label locationLabel = new Label("📍 " + attraction.getLocation() + " • ⏱️ " + attraction.getDuration());

        // Difficulty
        Label difficultyLabel = new Label("Difficulty: " + attraction.getDifficulty());
        if ("Easy".equalsIgnoreCase(attraction.getDifficulty())) {
            difficultyLabel.getStyleClass().add("difficulty-easy");
        }

        // Description
        Label descriptionLabel = new Label(attraction.getDescription());
        descriptionLabel.getStyleClass().add("attraction-description");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(320);

        // Price and Book button
        VBox bottomSection = new VBox(10);
        Label priceLabel = new Label("$" + String.format("%.0f", attraction.getPrice()) + "/day");
        priceLabel.getStyleClass().add("price-label");

        Button bookButton = new Button("Book Now");
        bookButton.getStyleClass().add("book-button");
        bookButton.setOnAction(e -> openBookingDialog(attraction));

        bottomSection.getChildren().addAll(priceLabel, bookButton);

        card.getChildren().addAll(imageView, titleLabel, locationLabel, difficultyLabel,
                descriptionLabel, bottomSection);

        return card;
    }

    private String getImageForAttraction(String attractionName) {
        if (attractionName.toLowerCase().contains("everest")) return "new.jpg";
        if (attractionName.toLowerCase().contains("chitwan")) return "chitwan.jpg";
        if (attractionName.toLowerCase().contains("kathmandu")) return "kathmandu.jpg";
        if (attractionName.toLowerCase().contains("pokhara")) return "new.jpg";
        return "new.jpg"; // default image
    }

    private void openBookingDialog(Attraction attraction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/cleanprj/views/BookingDialog.fxml"));
            Parent root = loader.load();

            BookingsDialogController controller = loader.getController();
            controller.setAttractionData(attraction);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Book Attraction - " + attraction.getName());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(destinationCards.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            // Refresh data when dialog closes
            dialogStage.setOnHidden(e -> {
                // Always refresh bookings and profile when dialog closes
                loadBookings();
                if (profileName != null) {
                    loadUserProfile();
                }

                // Show success message if booking was created
                if (controller.wasBookingCreated()) {
                    System.out.println("Booking created successfully - data refreshed");
                }
            });

            dialogStage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error opening booking dialog: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to open booking dialog: " + e.getMessage());
        }
    }

    private void loadBookings() {
        try {
            User currentUser = SessionManager.getCurrentUser();
            if (currentUser == null) {
                System.err.println("No current user found");
                return;
            }

            System.out.println("Loading bookings for user: " + currentUser.getId());
            List<Booking> userBookings = bookingDAO.findByTouristId(currentUser.getId());
            System.out.println("Found " + userBookings.size() + " bookings");

            if (bookingList != null) {
                bookingList.getChildren().clear();

                if (userBookings.isEmpty()) {
                    Label noBookingsLabel = new Label("No bookings found. Start exploring to make your first booking!");
                    noBookingsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666; -fx-padding: 20;");
                    bookingList.getChildren().add(noBookingsLabel);
                } else {
                    for (Booking booking : userBookings) {
                        VBox bookingCard = createBookingCard(booking);
                        bookingList.getChildren().add(bookingCard);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading bookings: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to load bookings: " + e.getMessage());
        }
    }

    private VBox createBookingCard(Booking booking) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-border-color: #e0e0e0; " +
                "-fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefWidth(1000);

        // Header with attraction name and status
        javafx.scene.layout.HBox headerBox = new javafx.scene.layout.HBox(10);
        headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label attractionLabel = new Label(booking.getAttractionName());
        attractionLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label statusLabel = new Label("Status: " + booking.getStatus());
        String statusColor = getStatusColor(booking.getStatus());
        statusLabel.setStyle("-fx-text-fill: " + statusColor + "; -fx-font-weight: bold;");

        // Add spacer
        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        javafx.scene.layout.HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        headerBox.getChildren().addAll(attractionLabel, spacer, statusLabel);

        // Dates
        Label datesLabel = new Label("📅 " + booking.getStartDate().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")) +
                " to " + booking.getEndDate().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")));

        // Guide info
        Label guideLabel = new Label("👨‍🏫 Guide: " + (booking.getGuideName() != null ? booking.getGuideName() : "To be assigned"));

        // Price
        Label priceLabel = new Label("💰 Total: $" + String.format("%.2f", booking.getTotalPrice()));
        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");

        // Contact info
        Label contactLabel = new Label("📞 Contact: " + booking.getTouristContact());

        // Booking date
        Label bookingDateLabel = new Label("📝 Booked on: " + booking.getBookingDate().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        bookingDateLabel.setStyle("-fx-text-fill: #666;");

        card.getChildren().addAll(headerBox, datesLabel, guideLabel, priceLabel, contactLabel, bookingDateLabel);

        // Notes if any
        if (booking.getNotes() != null && !booking.getNotes().trim().isEmpty()) {
            Label notesLabel = new Label("📝 Notes: " + booking.getNotes());
            notesLabel.setWrapText(true);
            notesLabel.setStyle("-fx-text-fill: #555;");
            card.getChildren().add(notesLabel);
        }

        // Action buttons section - ALWAYS create this section
        javafx.scene.layout.HBox actionBox = new javafx.scene.layout.HBox(10);
        actionBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        actionBox.setPadding(new javafx.geometry.Insets(10, 0, 0, 0));

        // Check booking status and add appropriate buttons/labels
        String status = booking.getStatus().toLowerCase();

        if (status.equals("pending") || status.equals("confirmed")) {
            // Add cancel button for bookings that can be cancelled
            Button cancelButton = new Button("Cancel Booking");
            cancelButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8 16; -fx-cursor: hand;");

            cancelButton.setOnAction(e -> {
                // Show confirmation dialog
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Cancel Booking");
                confirmAlert.setHeaderText("Are you sure you want to cancel this booking?");
                confirmAlert.setContentText("Booking: " + booking.getAttractionName() + "\n" +
                        "Dates: " + booking.getStartDate() + " to " + booking.getEndDate() + "\n" +
                        "Total: $" + String.format("%.2f", booking.getTotalPrice()) + "\n\n" +
                        "This action cannot be undone.");

                confirmAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        cancelBooking(booking);
                    }
                });
            });

            actionBox.getChildren().add(cancelButton);

        } else if (status.equals("completed")) {
            // Add status indicator for completed bookings
            Label completedLabel = new Label("✅ Trip Completed");
            completedLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold; -fx-font-size: 14px;");
            actionBox.getChildren().add(completedLabel);

        } else if (status.equals("cancelled")) {
            // Add status indicator for cancelled bookings
            Label cancelledLabel = new Label("❌ Booking Cancelled");
            cancelledLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold; -fx-font-size: 14px;");
            actionBox.getChildren().add(cancelledLabel);
        }

        // Always add the action box to the card
        card.getChildren().add(actionBox);

        return card;
    }

    private String getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "confirmed": return "#2e7d32";
            case "pending": return "#f57c00";
            case "cancelled": return "#d32f2f";
            case "completed": return "#1976d2";
            default: return "#666";
        }
    }

    private void loadUserProfile() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            // Update basic profile information
            if (profileName != null) {
                profileName.setText(capitalizeFirstLetter(currentUser.getFullName()));
            }
            if (profileEmail != null) {
                profileEmail.setText(currentUser.getEmail());
            }
            if (profileNationality != null) {
                profileNationality.setText(capitalizeFirstLetter(currentUser.getNationality()));
            }

            // Count total bookings
            try {
                List<Booking> userBookings = bookingDAO.findByTouristId(currentUser.getId());
                if (profileTotalBookings != null) {
                    profileTotalBookings.setText(String.valueOf(userBookings.size()));
                }
            } catch (IOException e) {
                if (profileTotalBookings != null) {
                    profileTotalBookings.setText("0");
                }
            }

            // Load emergency contact if exists
            if (emergencyContactField != null) {
                String emergencyContact = currentUser.getEmergencyContact();
                if (emergencyContact != null && !emergencyContact.trim().isEmpty()) {
                    emergencyContactField.setText(emergencyContact);
                } else {
                    emergencyContactField.setPromptText("Enter emergency contact number");
                }
            }
        }
    }

    private void updateEmergencyContact() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            showAlert("Error", "No user session found. Please login again.");
            return;
        }

        if (emergencyContactField == null) {
            showAlert("Error", "Emergency contact field not found.");
            return;
        }

        String emergencyContact = emergencyContactField.getText().trim();

        // Validate emergency contact
        if (emergencyContact.isEmpty()) {
            showAlert("Validation Error", "Please enter an emergency contact number.");
            emergencyContactField.requestFocus();
            return;
        }

        // Validate phone number format
        if (!emergencyContact.matches("^[+]?[0-9\\s\\-()]{7,20}$")) {
            showAlert("Validation Error", "Please enter a valid phone number.\n\n" +
                    "Examples:\n" +
                    "• +977-1-4247041\n" +
                    "• 9841234567\n" +
                    "• +1-555-123-4567");
            emergencyContactField.requestFocus();
            return;
        }

        try {
            // Update user's emergency contact
            currentUser.setEmergencyContact(emergencyContact);

            // Save to database
            userDAO.update(currentUser);

            // Update session
            SessionManager.setCurrentUser(currentUser);

            // Refresh profile display
            loadUserProfile();

            // Show success message
            showAlert("Success", "✅ Emergency contact updated successfully!\n\n" +
                    "📞 Contact: " + emergencyContact + "\n\n" +
                    "This information will be used for safety purposes during your trips.");

        } catch (IOException e) {
            System.err.println("Error updating emergency contact: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to update emergency contact. Please try again.\n\nError: " + e.getMessage());
        }
    }

    @FXML
    private void logout() {
        try {
            SessionManager.logout();

            // Load login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/cleanprj/views/hello-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) exploreTab.getScene().getWindow();
            Scene scene = new Scene(root, 1440, 850);
            String css = getClass().getResource("/org/example/cleanprj/styles/style.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.setTitle("Nepal Tourism Management System");

        } catch (IOException e) {
            System.err.println("Error during logout: " + e.getMessage());
            showAlert("Error", "Failed to logout: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void cancelBooking(Booking booking) {
        try {
            // Update booking status to cancelled
            booking.setStatus("Cancelled");
            bookingDAO.update(booking);

            // Refresh the bookings display
            loadBookings();

            // Refresh profile data as well
            if (profileName != null) {
                loadUserProfile();
            }

            // Show success message
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Booking Cancelled");
            successAlert.setHeaderText("Your booking has been cancelled successfully");
            successAlert.setContentText("Booking for " + booking.getAttractionName() + " has been cancelled.\n" +
                    "If you paid any advance, please contact our support team for refund processing.");
            successAlert.showAndWait();

        } catch (IOException e) {
            System.err.println("Error cancelling booking: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to cancel booking. Please try again.\n\nError: " + e.getMessage());
        }
    }
}
