package org.example.cleanprj.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.cleanprj.dao.*;
import org.example.cleanprj.models.*;
import org.example.cleanprj.services.DataExportService;
import org.example.cleanprj.utils.SceneChanger;
import org.example.cleanprj.utils.SessionManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AdminController implements Initializable {

    @FXML private ToggleButton analyticsTab;
    @FXML private ToggleButton touristTab;
    @FXML private ToggleButton guideTab;
    @FXML private ToggleButton attractionTab;
    @FXML private ToggleButton bookingTab;
    @FXML private ToggleButton emergencyTab;

    @FXML private VBox analyticsPane;
    @FXML private VBox touristPane;
    @FXML private VBox guidePane;
    @FXML private VBox attractionPane;
    @FXML private VBox bookingPane;
    @FXML private VBox emergencyPane;

    // Analytics Components
    @FXML private Button refreshAnalyticsButton;
    @FXML private Label lastUpdatedLabel;

    // Summary Cards
    @FXML private Label totalTouristsLabel;
    @FXML private Label touristGrowthLabel;
    @FXML private Label activeGuidesLabel;
    @FXML private Label guideStatusLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label bookingStatusLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label revenueStatusLabel;

    // Charts - Large Size without Individual Scrolling
    @FXML private PieChart mainPieChart;
    @FXML private BarChart<String, Number> popularAttractionsChart;
    @FXML private CategoryAxis barChartXAxis;
    @FXML private NumberAxis barChartYAxis;
    @FXML private ComboBox<String> chartTypeComboBox;
    @FXML private ComboBox<String> barChartTypeComboBox;

    // Tourist Table
    @FXML private TableView<User> touristTable;
    @FXML private TableColumn<User, String> touristNameColumn;
    @FXML private TableColumn<User, String> touristNationalityColumn;
    @FXML private TableColumn<User, String> touristContactColumn;
    @FXML private TableColumn<User, String> touristEmergencyColumn;
    @FXML private TableColumn<User, String> touristRegDateColumn;
    @FXML private TableColumn<User, String> touristStatusColumn;
    @FXML private TableColumn<User, Void> touristActionColumn;
    @FXML private TextField touristSearchField;
    @FXML private ComboBox<String> touristNationalityFilter;

    // Guide Table
    @FXML private TableView<Guide> guideTable;
    @FXML private TableColumn<Guide, String> guideNameColumn;
    @FXML private TableColumn<Guide, String> guideLanguageColumn;
    @FXML private TableColumn<Guide, Integer> guideExperienceColumn;
    @FXML private TableColumn<Guide, String> guideSpecializationColumn;
    @FXML private TableColumn<Guide, Double> guideRatingColumn;
    @FXML private TableColumn<Guide, String> guideContactColumn;
    @FXML private TableColumn<Guide, String> guideStatusColumn;
    @FXML private TableColumn<Guide, Void> guideActionColumn;
    @FXML private TextField guideSearchField;
    @FXML private ComboBox<String> guideSpecializationFilter;

    // Attraction Table
    @FXML private TableView<Attraction> attractionTable;
    @FXML private TableColumn<Attraction, String> attractionNameColumn;
    @FXML private TableColumn<Attraction, String> attractionTypeColumn;
    @FXML private TableColumn<Attraction, String> attractionLocationColumn;
    @FXML private TableColumn<Attraction, String> attractionDifficultyColumn;
    @FXML private TableColumn<Attraction, String> attractionAltitudeColumn;
    @FXML private TableColumn<Attraction, String> attractionDurationColumn;
    @FXML private TableColumn<Attraction, String> attractionStatusColumn;
    @FXML private TableColumn<Attraction, Void> attractionActionColumn;
    @FXML private TextField attractionSearchField;
    @FXML private ComboBox<String> attractionTypeFilter;
    @FXML private ComboBox<String> attractionDifficultyFilter;

    // Booking Table
    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, String> bookingTouristColumn;
    @FXML private TableColumn<Booking, String> bookingAttractionColumn;
    @FXML private TableColumn<Booking, String> bookingDatesColumn;
    @FXML private TableColumn<Booking, Double> bookingCostColumn;
    @FXML private TableColumn<Booking, String> bookingStatusColumn;
    @FXML private TableColumn<Booking, String> bookingGuideColumn;
    @FXML private TableColumn<Booking, Void> bookingAssignColumn;
    @FXML private TableColumn<Booking, Void> bookingActionColumn;
    @FXML private TextField bookingSearchField;
    @FXML private ComboBox<String> bookingStatusFilter;
    @FXML private Button filterBookingsButton;

    // Emergency Table
    @FXML private TableView<EmergencyContact> emergencyTable;
    @FXML private TableColumn<EmergencyContact, String> emergencyNameColumn;
    @FXML private TableColumn<EmergencyContact, String> emergencyOrgColumn;
    @FXML private TableColumn<EmergencyContact, String> emergencyPhoneColumn;
    @FXML private TableColumn<EmergencyContact, String> emergencyTypeColumn;
    @FXML private TableColumn<EmergencyContact, String> emergencyLocationColumn;
    @FXML private TableColumn<EmergencyContact, String> emergency24_7Column;

    // DAOs
    private UserDAO userDAO;
    private GuideDAO guideDAO;
    private AttractionDAO attractionDAO;
    private BookingDAO bookingDAO;
    private EmergencyContactDAO emergencyDAO;
    private DataExportService exportService;

    // Data lists
    private ObservableList<User> touristList;
    private ObservableList<Guide> guideList;
    private ObservableList<Attraction> attractionList;
    private ObservableList<Booking> bookingList;
    private ObservableList<Booking> filteredBookingList;
    private ObservableList<EmergencyContact> emergencyList;

    // Analytics data cache
    private Map<String, Object> analyticsCache = new HashMap<>();
    private LocalDateTime lastAnalyticsUpdate;

    // Color arrays for charts
    private final String[] PIE_COLORS = {
            "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7",
            "#DDA0DD", "#98D8C8", "#F7DC6F", "#BB8FCE", "#85C1E9"
    };

    private final String[] BAR_COLORS = {
            "#FF9F43", "#10AC84", "#EE5A24", "#0984E3", "#A29BFE",
            "#FD79A8", "#FDCB6E", "#6C5CE7", "#00B894", "#E17055"
    };

    // Analytics Labels
    @FXML private Label totalGuidesLabel;
    @FXML private Label totalAttractionsLabel;
    @FXML private Label pendingBookingsLabel;
    @FXML private Label confirmedBookingsLabel;
    @FXML private Label emergencyContactsLabel;

    // Tourists Table
    @FXML private TableView<Tourist> touristsTable;
    @FXML private TableColumn<Tourist, String> touristEmailColumn;
    @FXML private TableColumn<Tourist, String> touristPhoneColumn;
    @FXML private TableColumn<Tourist, String> touristCountryColumn;

    // Guides Table
    @FXML private TableView<Guide> guidesTable;
    @FXML private TableColumn<Guide, String> guideLanguagesColumn;
    @FXML private TableColumn<Guide, Void> guideActionsColumn;

    // Attractions Table
    @FXML private TableView<Attraction> attractionsTable;
    @FXML private TableColumn<Attraction, String> attractionPriceColumn;
    @FXML private TableColumn<Attraction, Void> attractionActionsColumn;

    // Bookings Table
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, Void> bookingDetailsColumn;

    // Emergency Contacts Table
    @FXML private TableView<EmergencyContact> emergencyContactsTable;
    @FXML private TableColumn<EmergencyContact, String> emergencyServiceColumn;
    @FXML private TableColumn<EmergencyContact, String> emergencyContactColumn;
    @FXML private TableColumn<EmergencyContact, String> emergencyAvailabilityColumn;
    @FXML private TableColumn<EmergencyContact, String> emergencyDescriptionColumn;

    // Search and Filter Controls
    @FXML private ComboBox<String> attractionStatusFilter;
    // Buttons
    @FXML private Button logoutButton;

    // Data Access Objects
    private EmergencyContactDAO emergencyContactDAO;

    // Data Lists
    private ObservableList<Tourist> allTourists = FXCollections.observableArrayList();
    private ObservableList<Guide> allGuides = FXCollections.observableArrayList();
    private ObservableList<Attraction> allAttractions = FXCollections.observableArrayList();
    private ObservableList<Booking> allBookings = FXCollections.observableArrayList();
    private ObservableList<EmergencyContact> allEmergencyContacts = FXCollections.observableArrayList();

    public void initialize() {
        initializeDAOs();
        initializeTables();
        setupTabNavigation();
        setupAnalyticsControls();
        setupChartStyling();
        setupTouristSearchAndFilter();
        setupGuideSearchAndFilter();
        setupAttractionSearchAndFilter();
        setupBookingSearchAndFilter();
        loadAllData();
        refreshAnalytics();
        showPane(analyticsPane);
        setActiveTab(analyticsTab);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeDAOs();
        initializeTables();
        initializeFilters();
        loadAllData();
        refreshAnalytics();
    }

    private void initializeDAOs() {
        userDAO = new UserDAO();
        guideDAO = new GuideDAO();
        attractionDAO = new AttractionDAO();
        bookingDAO = new BookingDAO();
        emergencyDAO = new EmergencyContactDAO();
        emergencyContactDAO = new EmergencyContactDAO();
        exportService = new DataExportService();
    }

    private void setupAnalyticsControls() {
        // Set default values for combo boxes
        if (chartTypeComboBox != null) {
            chartTypeComboBox.setValue("Tourists by Nationality");
        }
        if (barChartTypeComboBox != null) {
            barChartTypeComboBox.setValue("Top Attractions");
        }
    }

    private void setupChartStyling() {
        // Enhanced styling for larger charts
        if (mainPieChart != null) {
            mainPieChart.setLegendVisible(true);
            mainPieChart.setLabelsVisible(true);
            mainPieChart.setStartAngle(90);
            mainPieChart.setClockwise(true);
            mainPieChart.setStyle("-fx-font-size: 14px;");
        }

        if (popularAttractionsChart != null) {
            popularAttractionsChart.setLegendVisible(true);
            popularAttractionsChart.setAnimated(true);
            popularAttractionsChart.setStyle("-fx-font-size: 14px;");

            // Style the axes for better readability
            if (barChartXAxis != null) {
                barChartXAxis.setTickLabelRotation(-45);
                barChartXAxis.setStyle("-fx-font-size: 12px;");
            }
            if (barChartYAxis != null) {
                barChartYAxis.setStyle("-fx-font-size: 12px;");
            }
        }
    }

    private void setupTouristSearchAndFilter() {
        // Setup search functionality
        if (touristSearchField != null) {
            touristSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterTourists();
            });
        }

        // Setup nationality filter
        if (touristNationalityFilter != null) {
            touristNationalityFilter.setOnAction(e -> filterTourists());
        }
    }

    private void setupGuideSearchAndFilter() {
        // Setup search functionality for guides
        if (guideSearchField != null) {
            guideSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterGuides();
            });
        }

        // Setup specialization filter for guides
        if (guideSpecializationFilter != null) {
            guideSpecializationFilter.setOnAction(e -> filterGuides());
        }
    }

    private void setupAttractionSearchAndFilter() {
        // Setup search functionality for attractions
        if (attractionSearchField != null) {
            attractionSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterAttractions();
            });
        }

        // Setup type and difficulty filters for attractions
        if (attractionTypeFilter != null) {
            attractionTypeFilter.setOnAction(e -> filterAttractions());
        }
        if (attractionDifficultyFilter != null) {
            attractionDifficultyFilter.setOnAction(e -> filterAttractions());
        }
    }

    private void setupBookingSearchAndFilter() {
        // Setup search functionality for bookings
        if (bookingSearchField != null) {
            bookingSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterBookings();
            });
        }

        // Setup status filter for bookings
        if (bookingStatusFilter != null) {
            bookingStatusFilter.setOnAction(e -> filterBookings());
        }
    }

    private void filterTourists() {
        if (touristList == null) return;

        String searchText = touristSearchField.getText().toLowerCase().trim();
        String selectedNationality = touristNationalityFilter.getValue();

        ObservableList<User> filteredList = touristList.stream()
                .filter(tourist -> {
                    // Search filter
                    boolean matchesSearch = searchText.isEmpty() ||
                            tourist.getFullName().toLowerCase().contains(searchText) ||
                            tourist.getEmail().toLowerCase().contains(searchText) ||
                            (tourist.getEmergencyContact() != null &&
                                    tourist.getEmergencyContact().toLowerCase().contains(searchText));

                    // Nationality filter
                    boolean matchesNationality = selectedNationality == null ||
                            selectedNationality.equals("All Nationalities") ||
                            tourist.getNationality().equals(selectedNationality);

                    return matchesSearch && matchesNationality;
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        touristTable.setItems(filteredList);
    }

    private void filterGuides() {
        if (guideList == null) return;

        String searchText = guideSearchField.getText().toLowerCase().trim();
        String selectedSpecialization = guideSpecializationFilter.getValue();

        ObservableList<Guide> filteredList = guideList.stream()
                .filter(guide -> {
                    // Search filter
                    boolean matchesSearch = searchText.isEmpty() ||
                            guide.getName().toLowerCase().contains(searchText) ||
                            guide.getLanguages().toLowerCase().contains(searchText) ||
                            guide.getContact().toLowerCase().contains(searchText) ||
                            guide.getSpecialization().toLowerCase().contains(searchText);

                    // Specialization filter
                    boolean matchesSpecialization = selectedSpecialization == null ||
                            selectedSpecialization.equals("All Specializations") ||
                            guide.getSpecialization().equals(selectedSpecialization);

                    return matchesSearch && matchesSpecialization;
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        guideTable.setItems(filteredList);
    }

    private void filterAttractions() {
        if (attractionList == null) return;

        String searchText = attractionSearchField.getText().toLowerCase().trim();
        String selectedType = attractionTypeFilter.getValue();
        String selectedDifficulty = attractionDifficultyFilter.getValue();

        ObservableList<Attraction> filteredList = attractionList.stream()
                .filter(attraction -> {
                    // Search filter
                    boolean matchesSearch = searchText.isEmpty() ||
                            attraction.getName().toLowerCase().contains(searchText) ||
                            attraction.getLocation().toLowerCase().contains(searchText) ||
                            attraction.getType().toLowerCase().contains(searchText) ||
                            attraction.getDescription().toLowerCase().contains(searchText);

                    // Type filter
                    boolean matchesType = selectedType == null ||
                            selectedType.equals("All Types") ||
                            attraction.getType().equals(selectedType);

                    // Difficulty filter
                    boolean matchesDifficulty = selectedDifficulty == null ||
                            selectedDifficulty.equals("All Difficulties") ||
                            attraction.getDifficulty().equals(selectedDifficulty);

                    return matchesSearch && matchesType && matchesDifficulty;
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        attractionTable.setItems(filteredList);
    }

    @FXML
    private void filterBookings() {
        if (bookingList == null) return;

        String searchText = bookingSearchField != null ? bookingSearchField.getText().toLowerCase().trim() : "";
        String selectedStatus = bookingStatusFilter != null ? bookingStatusFilter.getValue() : null;

        filteredBookingList = bookingList.stream()
                .filter(booking -> {
                    // Search filter
                    boolean matchesSearch = searchText.isEmpty() ||
                            booking.getTouristName().toLowerCase().contains(searchText) ||
                            booking.getAttractionName().toLowerCase().contains(searchText) ||
                            (booking.getGuideName() != null && booking.getGuideName().toLowerCase().contains(searchText));

                    // Status filter
                    boolean matchesStatus = selectedStatus == null ||
                            selectedStatus.equals("All Statuses") ||
                            booking.getStatus().equals(selectedStatus);

                    return matchesSearch && matchesStatus;
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        if (bookingTable != null) {
            bookingTable.setItems(filteredBookingList);
        }

        System.out.println("Filtered bookings: " + filteredBookingList.size() + " out of " + bookingList.size());
    }

    private void initializeTables() {
        // Tourist Table
        if (touristTable != null) {
            touristNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
            touristNationalityColumn.setCellValueFactory(new PropertyValueFactory<>("nationality"));
            touristContactColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            touristEmergencyColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getEmergencyContact() != null ?
                            cellData.getValue().getEmergencyContact() : "Not provided"));
            touristRegDateColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getRegistrationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
            touristStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty("Active"));

            // Setup Action Column with Remove Button
            touristActionColumn.setCellFactory(new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
                @Override
                public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                    final TableCell<User, Void> cell = new TableCell<User, Void>() {
                        private final Button removeButton = new Button("Remove");

                        {
                            removeButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5 10;");
                            removeButton.setOnAction((ActionEvent event) -> {
                                User tourist = getTableView().getItems().get(getIndex());
                                removeTourist(tourist);
                            });
                        }

                        @Override
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(removeButton);
                            }
                        }
                    };
                    return cell;
                }
            });

            // Add row selection listener for tourist details
            touristTable.setRowFactory(tv -> {
                TableRow<User> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        User selectedTourist = row.getItem();
                        showTouristDetails(selectedTourist);
                    }
                });
                return row;
            });
        }

        // Guide Table
        if (guideTable != null) {
            guideNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            guideLanguageColumn.setCellValueFactory(new PropertyValueFactory<>("languages"));
            guideExperienceColumn.setCellValueFactory(new PropertyValueFactory<>("experience"));
            guideSpecializationColumn.setCellValueFactory(new PropertyValueFactory<>("specialization"));
            guideRatingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
            guideContactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
            guideStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

            // Setup Action Column with Remove and Update Buttons
            guideActionColumn.setCellFactory(new Callback<TableColumn<Guide, Void>, TableCell<Guide, Void>>() {
                @Override
                public TableCell<Guide, Void> call(final TableColumn<Guide, Void> param) {
                    final TableCell<Guide, Void> cell = new TableCell<Guide, Void>() {
                        private final Button updateButton = new Button("Update");
                        private final Button removeButton = new Button("Remove");
                        private final HBox buttonBox = new HBox(5);

                        {
                            // Style the buttons
                            updateButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8;");
                            removeButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8;");

                            buttonBox.setAlignment(Pos.CENTER);
                            buttonBox.getChildren().addAll(updateButton, removeButton);

                            // Button actions
                            updateButton.setOnAction((ActionEvent event) -> {
                                Guide guide = getTableView().getItems().get(getIndex());
                                updateGuide(guide);
                            });

                            removeButton.setOnAction((ActionEvent event) -> {
                                Guide guide = getTableView().getItems().get(getIndex());
                                removeGuide(guide);
                            });
                        }

                        @Override
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(buttonBox);
                            }
                        }
                    };
                    return cell;
                }
            });

            // Add row selection listener for guide details
            guideTable.setRowFactory(tv -> {
                TableRow<Guide> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        Guide selectedGuide = row.getItem();
                        showGuideDetails(selectedGuide);
                    }
                });
                return row;
            });
        }

        // Attraction Table
        if (attractionTable != null) {
            attractionNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            attractionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
            attractionLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
            attractionDifficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
            attractionAltitudeColumn.setCellValueFactory(new PropertyValueFactory<>("altitude"));
            attractionDurationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
            attractionStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

            // Setup Action Column with Update and Remove Buttons for Attractions
            attractionActionColumn.setCellFactory(new Callback<TableColumn<Attraction, Void>, TableCell<Attraction, Void>>() {
                @Override
                public TableCell<Attraction, Void> call(final TableColumn<Attraction, Void> param) {
                    final TableCell<Attraction, Void> cell = new TableCell<Attraction, Void>() {
                        private final Button updateButton = new Button("Update");
                        private final Button removeButton = new Button("Remove");
                        private final HBox buttonBox = new HBox(5);

                        {
                            // Style the buttons
                            updateButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8;");
                            removeButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8;");

                            buttonBox.setAlignment(Pos.CENTER);
                            buttonBox.getChildren().addAll(updateButton, removeButton);

                            // Button actions
                            updateButton.setOnAction((ActionEvent event) -> {
                                Attraction attraction = getTableView().getItems().get(getIndex());
                                updateAttraction(attraction);
                            });

                            removeButton.setOnAction((ActionEvent event) -> {
                                Attraction attraction = getTableView().getItems().get(getIndex());
                                removeAttraction(attraction);
                            });
                        }

                        @Override
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(buttonBox);
                            }
                        }
                    };
                    return cell;
                }
            });

            // Add row selection listener for attraction details
            attractionTable.setRowFactory(tv -> {
                TableRow<Attraction> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        Attraction selectedAttraction = row.getItem();
                        showAttractionDetails(selectedAttraction);
                    }
                });
                return row;
            });
        }

        // Booking Table - Enhanced with Assign Guide functionality
        if (bookingTable != null) {
            bookingTouristColumn.setCellValueFactory(new PropertyValueFactory<>("touristName"));
            bookingAttractionColumn.setCellValueFactory(new PropertyValueFactory<>("attractionName"));
            bookingDatesColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getStartDate() + " to " + cellData.getValue().getEndDate()));
            bookingCostColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
            bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            bookingGuideColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getGuideName() != null ? cellData.getValue().getGuideName() : "Not Assigned"));

            // Setup Assign Guide Column
            bookingAssignColumn.setCellFactory(new Callback<TableColumn<Booking, Void>, TableCell<Booking, Void>>() {
                @Override
                public TableCell<Booking, Void> call(final TableColumn<Booking, Void> param) {
                    final TableCell<Booking, Void> cell = new TableCell<Booking, Void>() {
                        private final Button assignButton = new Button("Assign Guide");

                        {
                            assignButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8;");
                            assignButton.setOnAction((ActionEvent event) -> {
                                Booking booking = getTableView().getItems().get(getIndex());
                                openGuideAssignmentDialog(booking);
                            });
                        }

                        @Override
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                Booking booking = getTableView().getItems().get(getIndex());
                                if (booking != null) {
                                    // Show assign button only for pending bookings or bookings without guides
                                    if ("Pending".equals(booking.getStatus()) || booking.getGuideName() == null) {
                                        assignButton.setText(booking.getGuideName() == null ? "Assign Guide" : "Change Guide");
                                        setGraphic(assignButton);
                                    } else {
                                        setGraphic(null);
                                    }
                                }
                            }
                        }
                    };
                    return cell;
                }
            });

            // Setup Action Column for booking management
            bookingActionColumn.setCellFactory(new Callback<TableColumn<Booking, Void>, TableCell<Booking, Void>>() {
                @Override
                public TableCell<Booking, Void> call(final TableColumn<Booking, Void> param) {
                    final TableCell<Booking, Void> cell = new TableCell<Booking, Void>() {
                        private final Button detailsButton = new Button("Details");

                        {
                            detailsButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 11px; -fx-padding: 4 8;");
                            detailsButton.setOnAction((ActionEvent event) -> {
                                Booking booking = getTableView().getItems().get(getIndex());
                                showBookingDetails(booking);
                            });
                        }

                        @Override
                        public void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(detailsButton);
                            }
                        }
                    };
                    return cell;
                }
            });

            // Add row selection listener for booking details
            bookingTable.setRowFactory(tv -> {
                TableRow<Booking> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        Booking selectedBooking = row.getItem();
                        showBookingDetails(selectedBooking);
                    }
                });
                return row;
            });
        }

        // Emergency Table
        if (emergencyTable != null) {
            emergencyNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            emergencyOrgColumn.setCellValueFactory(new PropertyValueFactory<>("organization"));
            emergencyPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
            emergencyTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
            emergencyLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
            emergency24_7Column.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().isAvailable24_7() ? "Yes" : "No"));
        }
    }

    private void initializeTouristsTable() {
        touristNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        touristEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        touristPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        touristCountryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        touristStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void initializeGuidesTable() {
        guideNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        guideLanguagesColumn.setCellValueFactory(new PropertyValueFactory<>("languages"));
        guideSpecializationColumn.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        guideExperienceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getExperience() + " years"));
        guideRatingColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.1f", cellData.getValue().getRating())));
        guideContactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        guideStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add action buttons for guides
        guideActionsColumn.setCellFactory(param -> new TableCell<Guide, Void>() {
            private final Button updateButton = new Button("Update");
            private final Button removeButton = new Button("Remove");

            {
                updateButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 10px;");
                removeButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 10px;");

                updateButton.setOnAction(event -> {
                    Guide guide = getTableView().getItems().get(getIndex());
                    handleUpdateGuide(guide);
                });

                removeButton.setOnAction(event -> {
                    Guide guide = getTableView().getItems().get(getIndex());
                    handleRemoveGuide(guide);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new javafx.scene.layout.HBox(5, updateButton, removeButton));
                }
            }
        });
    }

    private void initializeAttractionsTable() {
        attractionNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        attractionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        attractionLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        attractionDifficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        attractionPriceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getPrice())));
        attractionStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add action buttons for attractions
        attractionActionsColumn.setCellFactory(param -> new TableCell<Attraction, Void>() {
            private final Button updateButton = new Button("Update");
            private final Button removeButton = new Button("Remove");

            {
                updateButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 10px;");
                removeButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 10px;");

                updateButton.setOnAction(event -> {
                    Attraction attraction = getTableView().getItems().get(getIndex());
                    handleUpdateAttraction(attraction);
                });

                removeButton.setOnAction(event -> {
                    Attraction attraction = getTableView().getItems().get(getIndex());
                    handleRemoveAttraction(attraction);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new javafx.scene.layout.HBox(5, updateButton, removeButton));
                }
            }
        });
    }

    private void initializeBookingsTable() {
        bookingTouristColumn.setCellValueFactory(new PropertyValueFactory<>("touristName"));
        bookingAttractionColumn.setCellValueFactory(new PropertyValueFactory<>("attractionName"));
        bookingDatesColumn.setCellValueFactory(cellData -> {
            Booking booking = cellData.getValue();
            String dates = booking.getStartDate().format(DateTimeFormatter.ofPattern("MMM dd")) +
                    " - " + booking.getEndDate().format(DateTimeFormatter.ofPattern("MMM dd"));
            return new SimpleStringProperty(dates);
        });
        bookingCostColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getTotalCost())));
        bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        bookingGuideColumn.setCellValueFactory(cellData -> {
            String guideName = cellData.getValue().getGuideName();
            return new SimpleStringProperty(guideName != null ? guideName : "Not Assigned");
        });

        // Add assign guide button
        bookingAssignColumn.setCellFactory(param -> new TableCell<Booking, Void>() {
            private final Button assignButton = new Button("Assign Guide");

            {
                assignButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 10px;");
                assignButton.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    handleAssignGuide(booking);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Booking booking = getTableView().getItems().get(getIndex());
                    if (booking != null) {
                        if ("Completed".equals(booking.getStatus()) || "Cancelled".equals(booking.getStatus())) {
                            setGraphic(null);
                        } else {
                            if (booking.getGuideName() != null && !booking.getGuideName().isEmpty()) {
                                assignButton.setText("Change Guide");
                                assignButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 10px;");
                            } else {
                                assignButton.setText("Assign Guide");
                                assignButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 10px;");
                            }
                            setGraphic(assignButton);
                        }
                    }
                }
            }
        });

        // Add details button
        bookingDetailsColumn.setCellFactory(param -> new TableCell<Booking, Void>() {
            private final Button detailsButton = new Button("View");

            {
                detailsButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 10px;");
                detailsButton.setOnAction(event -> {
                    Booking booking = getTableView().getItems().get(getIndex());
                    handleViewBookingDetails(booking);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(detailsButton);
                }
            }
        });
    }

    private void initializeEmergencyContactsTable() {
        emergencyServiceColumn.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        emergencyContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        emergencyLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        emergencyAvailabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));
        emergencyDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    private void initializeFilters() {
        // Initialize attraction status filter
        attractionStatusFilter.setItems(FXCollections.observableArrayList(
                "All Statuses", "Active", "Inactive", "Under Maintenance"
        ));
        attractionStatusFilter.setValue("All Statuses");

        // Initialize booking status filter
        bookingStatusFilter.setItems(FXCollections.observableArrayList(
                "All Statuses", "Pending", "Confirmed", "Completed", "Cancelled"
        ));
        bookingStatusFilter.setValue("All Statuses");

        // Add real-time search functionality
        attractionSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAttractions();
        });

        bookingSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterBookings();
        });
    }

    private void loadAllData() {
        loadTouristData();
        loadGuideData();
        loadAttractionData();
        loadBookingData();
        loadEmergencyData();
    }

    private void loadTourists() {
        try {
            List<User> users = userDAO.findAll();
            allTourists.clear();

            for (User user : users) {
                if (user instanceof Tourist) {
                    allTourists.add((Tourist) user);
                }
            }

            touristsTable.setItems(allTourists);
            System.out.println("Loaded " + allTourists.size() + " tourists in admin panel");
        } catch (IOException e) {
            showAlert("Error", "Failed to load tourists: " + e.getMessage());
        }
    }

    private void loadGuideData() {
        try {
            List<Guide> guides = guideDAO.findAll();
            guideList = FXCollections.observableArrayList(guides);
            if (guideTable != null) {
                guideTable.setItems(guideList);
            }

            // Update specialization filter
            if (guideSpecializationFilter != null) {
                List<String> specializations = guides.stream()
                        .map(Guide::getSpecialization)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                ObservableList<String> specializationOptions = FXCollections.observableArrayList();
                specializationOptions.add("All Specializations");
                specializationOptions.addAll(specializations);

                guideSpecializationFilter.setItems(specializationOptions);
                guideSpecializationFilter.setValue("All Specializations");
            }

            System.out.println("Loaded " + guides.size() + " guides in admin panel");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load guide data: " + e.getMessage());
        }
    }

    private void loadGuides() {
        try {
            allGuides.clear();
            allGuides.addAll(guideDAO.findAll());
            guidesTable.setItems(allGuides);
            System.out.println("Loaded " + allGuides.size() + " guides in admin panel");
        } catch (IOException e) {
            showAlert("Error", "Failed to load guides: " + e.getMessage());
        }
    }

    private void loadAttractionData() {
        try {
            List<Attraction> attractions = attractionDAO.findAll();
            attractionList = FXCollections.observableArrayList(attractions);
            if (attractionTable != null) {
                attractionTable.setItems(attractionList);
            }

            // Update type and difficulty filters
            if (attractionTypeFilter != null) {
                List<String> types = attractions.stream()
                        .map(Attraction::getType)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                ObservableList<String> typeOptions = FXCollections.observableArrayList();
                typeOptions.add("All Types");
                typeOptions.addAll(types);

                attractionTypeFilter.setItems(typeOptions);
                attractionTypeFilter.setValue("All Types");
            }

            if (attractionDifficultyFilter != null) {
                List<String> difficulties = attractions.stream()
                        .map(Attraction::getDifficulty)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                ObservableList<String> difficultyOptions = FXCollections.observableArrayList();
                difficultyOptions.add("All Difficulties");
                difficultyOptions.addAll(difficulties);

                attractionDifficultyFilter.setItems(difficultyOptions);
                attractionDifficultyFilter.setValue("All Difficulties");
            }

            System.out.println("Loaded " + attractions.size() + " attractions in admin panel");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load attraction data: " + e.getMessage());
        }
    }

    private void loadAttractions() {
        try {
            allAttractions.clear();
            allAttractions.addAll(attractionDAO.findAll());
            attractionsTable.setItems(allAttractions);
            System.out.println("Loaded " + allAttractions.size() + " attractions in admin panel");
        } catch (IOException e) {
            showAlert("Error", "Failed to load attractions: " + e.getMessage());
        }
    }

    @FXML
    public void loadBookingData() {
        try {
            List<Booking> bookings = bookingDAO.findAll();
            bookingList = FXCollections.observableArrayList(bookings);
            if (bookingTable != null) {
                bookingTable.setItems(bookingList);
            }

            // Initialize filtered list
            filteredBookingList = FXCollections.observableArrayList(bookings);

            System.out.println("Loaded " + bookings.size() + " bookings in admin panel");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load booking data: " + e.getMessage());
        }
    }

    private void loadBookings() {
        try {
            allBookings.clear();
            allBookings.addAll(bookingDAO.findAll());
            bookingsTable.setItems(allBookings);
            System.out.println("Loaded " + allBookings.size() + " bookings in admin panel");
        } catch (IOException e) {
            showAlert("Error", "Failed to load bookings: " + e.getMessage());
        }
    }

    private void loadEmergencyData() {
        try {
            List<EmergencyContact> contacts = emergencyDAO.findAll();
            emergencyList = FXCollections.observableArrayList(contacts);
            if (emergencyTable != null) {
                emergencyTable.setItems(emergencyList);
            }
            System.out.println("Loaded " + contacts.size() + " emergency contacts in admin panel");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load emergency contact data: " + e.getMessage());
        }
    }

    private void loadEmergencyContacts() {
        try {
            allEmergencyContacts.clear();
            allEmergencyContacts.addAll(emergencyContactDAO.findAll());
            emergencyContactsTable.setItems(allEmergencyContacts);
            System.out.println("Loaded " + allEmergencyContacts.size() + " emergency contacts in admin panel");
        } catch (IOException e) {
            showAlert("Error", "Failed to load emergency contacts: " + e.getMessage());
        }
    }

    @FXML
    private void refreshAnalytics() {
        System.out.println("Refreshing analytics data with colorful charts...");

        // Reload all data first
        loadAllData();

        // Update summary cards
        updateSummaryCards();

        // Update charts with colorful styling
        updatePieChart();
        updateBarChart();

        // Update last updated timestamp
        lastAnalyticsUpdate = LocalDateTime.now();
        if (lastUpdatedLabel != null) {
            lastUpdatedLabel.setText("Last updated: " +
                    lastAnalyticsUpdate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        }

        System.out.println("Colorful analytics refresh completed successfully");
    }

    @FXML
    private void refreshAnalytics() {
        System.out.println("Refreshing analytics data with colorful charts...");

        // Reload all data first
        loadAllData();

        // Update analytics labels
        totalTouristsLabel.setText(String.valueOf(allTourists.size()));
        totalGuidesLabel.setText(String.valueOf(allGuides.size()));
        totalAttractionsLabel.setText(String.valueOf(allAttractions.size()));
        totalBookingsLabel.setText(String.valueOf(allBookings.size()));

        // Calculate booking statistics
        long pendingCount = allBookings.stream().filter(b -> "Pending".equals(b.getStatus())).count();
        long confirmedCount = allBookings.stream().filter(b -> "Confirmed".equals(b.getStatus())).count();

        pendingBookingsLabel.setText(String.valueOf(pendingCount));
        confirmedBookingsLabel.setText(String.valueOf(confirmedCount));

        // Calculate total revenue
        double totalRevenue = allBookings.stream()
                .filter(b -> !"Cancelled".equals(b.getStatus()))
                .mapToDouble(Booking::getTotalCost)
                .sum();
        totalRevenueLabel.setText(String.format("$%.2f", totalRevenue));

        emergencyContactsLabel.setText(String.valueOf(allEmergencyContacts.size()));

        System.out.println("Colorful analytics refresh completed successfully");
    }

    @FXML
    private void filterAttractions() {
        String searchText = attractionSearchField.getText().toLowerCase();
        String statusFilter = attractionStatusFilter.getValue();

        ObservableList<Attraction> filteredList = allAttractions.stream()
                .filter(attraction -> {
                    boolean matchesSearch = searchText.isEmpty() ||
                            attraction.getName().toLowerCase().contains(searchText) ||
                            attraction.getLocation().toLowerCase().contains(searchText) ||
                            attraction.getType().toLowerCase().contains(searchText);

                    boolean matchesStatus = "All Statuses".equals(statusFilter) ||
                            attraction.getStatus().equals(statusFilter);

                    return matchesSearch && matchesStatus;
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        attractionsTable.setItems(filteredList);
    }

    @FXML
    private void filterBookings() {
        String searchText = bookingSearchField.getText().toLowerCase();
        String statusFilter = bookingStatusFilter.getValue();

        ObservableList<Booking> filteredList = allBookings.stream()
                .filter(booking -> {
                    boolean matchesSearch = searchText.isEmpty() ||
                            booking.getTouristName().toLowerCase().contains(searchText) ||
                            booking.getAttractionName().toLowerCase().contains(searchText) ||
                            (booking.getGuideName() != null && booking.getGuideName().toLowerCase().contains(searchText));

                    boolean matchesStatus = "All Statuses".equals(statusFilter) ||
                            booking.getStatus().equals(statusFilter);

                    return matchesSearch && matchesStatus;
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        bookingsTable.setItems(filteredList);
        System.out.println("Filtered bookings: " + filteredList.size() + " out of " + allBookings.size());
    }

    @FXML
    private void refreshBookings() {
        loadBookings();
        filterBookings(); // Reapply current filters
        refreshAnalytics();
        showAlert("Success", "Bookings data refreshed successfully!");
    }

    @FXML
    private void handleAssignGuide(Booking booking) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/cleanprj/views/GuideSelectionDialog.fxml"));
            Parent root = loader.load();

            GuideSelectionController controller = loader.getController();
            controller.setBooking(booking);
            controller.setAdminController(this);

            Stage stage = new Stage();
            stage.setTitle("Assign Guide to Booking");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            showAlert("Error", "Failed to open guide selection dialog: " + e.getMessage());
        }
    }

    public void onGuideAssigned(Booking booking, Guide guide) {
        try {
            // Update booking with guide information
            booking.setGuideId(guide.getId());
            booking.setGuideName(guide.getName());
            booking.setGuideContact(guide.getContact());
            booking.setStatus("Confirmed");

            // Save updated booking
            bookingDAO.update(booking);

            // Refresh the bookings table
            loadBookings();
            filterBookings();
            refreshAnalytics();

            System.out.println("Guide assigned: " + guide.getName() + " to booking: " + booking.getId());
            showAlert("Success", "Guide " + guide.getName() + " has been assigned to the booking successfully!");

        } catch (IOException e) {
            showAlert("Error", "Failed to assign guide: " + e.getMessage());
        }
    }

    private void handleViewBookingDetails(Booking booking) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Booking Details");
        alert.setHeaderText("Booking ID: " + booking.getId());

        StringBuilder details = new StringBuilder();
        details.append("Tourist: ").append(booking.getTouristName()).append("\n");
        details.append("Attraction: ").append(booking.getAttractionName()).append("\n");
        details.append("Start Date: ").append(booking.getStartDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))).append("\n");
        details.append("End Date: ").append(booking.getEndDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))).append("\n");
        details.append("Total Cost: $").append(String.format("%.2f", booking.getTotalCost())).append("\n");
        details.append("Status: ").append(booking.getStatus()).append("\n");
        details.append("Assigned Guide: ").append(booking.getGuideName() != null ? booking.getGuideName() : "Not Assigned").append("\n");
        if (booking.getGuideContact() != null) {
            details.append("Guide Contact: ").append(booking.getGuideContact()).append("\n");
        }
        details.append("Booking Date: ").append(booking.getBookingDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));

        alert.setContentText(details.toString());
        alert.showAndWait();
    }

    private void handleUpdateGuide(Guide guide) {
        // Implementation for updating guide
        showAlert("Info", "Update guide functionality - Coming soon!");
    }

    private void handleRemoveGuide(Guide guide) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Removal");
        confirmAlert.setHeaderText("Remove Guide");
        confirmAlert.setContentText("Are you sure you want to remove guide: " + guide.getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                guideDAO.delete(guide.getId());
                loadGuides();
                refreshAnalytics();
                showAlert("Success", "Guide removed successfully!");
            } catch (IOException e) {
                showAlert("Error", "Failed to remove guide: " + e.getMessage());
            }
        }
    }

    private void handleUpdateAttraction(Attraction attraction) {
        // Implementation for updating attraction
        showAlert("Info", "Update attraction functionality - Coming soon!");
    }

    private void handleRemoveAttraction(Attraction attraction) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Removal");
        confirmAlert.setHeaderText("Remove Attraction");
        confirmAlert.setContentText("Are you sure you want to remove attraction: " + attraction.getName() + "?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                attractionDAO.delete(attraction.getId());
                loadAttractions();
                refreshAnalytics();
                showAlert("Success", "Attraction removed successfully!");
            } catch (IOException e) {
                showAlert("Error", "Failed to remove attraction: " + e.getMessage());
            }
        }
    }

    @FXML
    private void showAddGuideDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/cleanprj/views/AddNewGuideDialog.fxml"));
            Parent root = loader.load();

            AddNewGuideController controller = loader.getController();
            controller.setAdminController(this);

            Stage stage = new Stage();
            stage.setTitle("Add New Guide");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Error", "Failed to open add guide dialog: " + e.getMessage());
        }
    }

    @FXML
    private void showAddAttractionDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/cleanprj/views/AddNewAttractionDialog.fxml"));
            Parent root = loader.load();

            AddNewAttractionController controller = loader.getController();
            controller.setAdminController(this);

            Stage stage = new Stage();
            stage.setTitle("Add New Attraction");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Error", "Failed to open add attraction dialog: " + e.getMessage());
        }
    }

    @FXML
    private void showAddEmergencyContactDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/cleanprj/views/AddEmergencyContactDialog.fxml"));
            Parent root = loader.load();

            AddEmergencyContactController controller = loader.getController();
            controller.setAdminController(this);

            Stage stage = new Stage();
            stage.setTitle("Add Emergency Contact");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            showAlert("Error", "Failed to open add emergency contact dialog: " + e.getMessage());
        }
    }

    // Export methods
    @FXML
    private void exportTourists() {
        exportData("tourists", allTourists);
    }

    @FXML
    private void exportGuides() {
        exportData("guides", allGuides);
    }

    @FXML
    private void exportAttractions() {
        exportData("attractions", allAttractions);
    }

    @FXML
    private void exportBookings() {
        exportData("bookings", allBookings);
    }

    @FXML
    private void exportEmergencyContacts() {
        exportData("emergency_contacts", allEmergencyContacts);
    }

    private void exportData(String dataType, ObservableList<?> data) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export " + dataType.replace("_", " ").toUpperCase());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName(dataType + "_export.csv");

        Stage stage = (Stage) logoutButton.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                exportService.exportToCSV(data, file.getAbsolutePath());
                showAlert("Success", dataType.replace("_", " ").toUpperCase() + " exported successfully to: " + file.getAbsolutePath());
            } catch (IOException e) {
                showAlert("Error", "Failed to export data: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleLogout() {
        try {
            SceneChanger.changeScene((Stage) logoutButton.getScene().getWindow(),
                    "/org/example/cleanprj/views/hello-view.fxml", "Tourism Management System");
        } catch (IOException e) {
            showAlert("Error", "Failed to logout: " + e.getMessage());
        }
    }

    public void refreshGuides() {
        loadGuides();
        refreshAnalytics();
    }

    public void refreshAttractions() {
        loadAttractions();
        refreshAnalytics();
    }

    public void refreshEmergencyContacts() {
        loadEmergencyContacts();
        refreshAnalytics();
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @FXML
    private void logout() {
        SessionManager.logout();
        try {
            Stage stage = (Stage) analyticsPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/cleanprj/views/hello-view.fxml"));
            Scene scene = new Scene(loader.load(), 1440, 850);
            String css = getClass().getResource("/org/example/cleanprj/styles/style.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateSummaryCards() {
        try {
            // Calculate real statistics
            List<User> tourists = userDAO.findAll().stream()
                    .filter(user -> "Tourist".equals(user.getRole()))
                    .collect(Collectors.toList());

            List<Guide> guides = guideDAO.findAll();
            List<Booking> bookings = bookingDAO.findAll();
            List<Attraction> attractions = attractionDAO.findAll();

            // Update tourist card
            if (totalTouristsLabel != null) {
                totalTouristsLabel.setText(String.valueOf(tourists.size()));
            }
            if (touristGrowthLabel != null) {
                long recentTourists = tourists.stream()
                        .filter(t -> t.getRegistrationDate().isAfter(LocalDateTime.now().minusDays(30)))
                        .count();
                touristGrowthLabel.setText("+" + recentTourists + " this month");
            }

            // Update guides card
            if (activeGuidesLabel != null) {
                long activeGuides = guides.stream()
                        .filter(g -> "Active".equals(g.getStatus()))
                        .count();
                activeGuidesLabel.setText(String.valueOf(activeGuides));
            }
            if (guideStatusLabel != null) {
                guideStatusLabel.setText(guides.size() + " total guides");
            }

            // Update bookings card
            if (totalBookingsLabel != null) {
                totalBookingsLabel.setText(String.valueOf(bookings.size()));
            }
            if (bookingStatusLabel != null) {
                long pendingBookings = bookings.stream()
                        .filter(b -> "Pending".equals(b.getStatus()))
                        .count();
                bookingStatusLabel.setText(pendingBookings + " pending");
            }

            // Update revenue card
            double totalRevenue = bookings.stream()
                    .filter(b -> !"Cancelled".equals(b.getStatus()))
                    .mapToDouble(Booking::getTotalPrice)
                    .sum();

            if (totalRevenueLabel != null) {
                totalRevenueLabel.setText("$" + String.format("%.2f", totalRevenue));

                totalRevenueLabel.setText("$" + String.format("%.2f", totalRevenue));
            }
            if (revenueStatusLabel != null) {
                double avgBookingValue = bookings.isEmpty() ? 0 : totalRevenue / bookings.size();
                revenueStatusLabel.setText("Avg: $" + String.format("%.2f", avgBookingValue));
            }

            // Cache the data
            analyticsCache.put("tourists", tourists);
            analyticsCache.put("guides", guides);
            analyticsCache.put("bookings", bookings);
            analyticsCache.put("attractions", attractions);
            analyticsCache.put("totalRevenue", totalRevenue);

        } catch (IOException e) {
            System.err.println("Error updating summary cards: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void changeChartType() {
        updatePieChart();
    }

    @FXML
    private void changeBarChartType() {
        updateBarChart();
    }

    private void updatePieChart() {
        try {
            if (mainPieChart == null || chartTypeComboBox == null) return;

            String chartType = chartTypeComboBox.getValue();
            if (chartType == null) chartType = "Tourists by Nationality";

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            switch (chartType) {
                case "Tourists by Nationality":
                    @SuppressWarnings("unchecked")
                    List<User> tourists = (List<User>) analyticsCache.get("tourists");
                    if (tourists == null) {
                        tourists = userDAO.findAll().stream()
                                .filter(user -> "Tourist".equals(user.getRole()))
                                .collect(Collectors.toList());
                    }

                    Map<String, Long> nationalityCount = tourists.stream()
                            .collect(Collectors.groupingBy(User::getNationality, Collectors.counting()));

                    for (Map.Entry<String, Long> entry : nationalityCount.entrySet()) {
                        pieChartData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
                    }
                    mainPieChart.setTitle("Tourist Distribution by Nationality");
                    break;

                case "Bookings by Status":
                    @SuppressWarnings("unchecked")
                    List<Booking> bookings = (List<Booking>) analyticsCache.get("bookings");
                    if (bookings == null) bookings = bookingDAO.findAll();

                    Map<String, Long> statusCount = bookings.stream()
                            .collect(Collectors.groupingBy(Booking::getStatus, Collectors.counting()));

                    for (Map.Entry<String, Long> entry : statusCount.entrySet()) {
                        pieChartData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
                    }
                    mainPieChart.setTitle("Booking Status Distribution");
                    break;

                case "Attractions by Type":
                    @SuppressWarnings("unchecked")
                    List<Attraction> attractions = (List<Attraction>) analyticsCache.get("attractions");
                    if (attractions == null) attractions = attractionDAO.findAll();

                    Map<String, Long> typeCount = attractions.stream()
                            .collect(Collectors.groupingBy(Attraction::getType, Collectors.counting()));

                    for (Map.Entry<String, Long> entry : typeCount.entrySet()) {
                        pieChartData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
                    }
                    mainPieChart.setTitle("Attraction Type Distribution");
                    break;
            }

            mainPieChart.setData(pieChartData);

            // Apply different colors to pie chart segments
            applyPieChartColors();

        } catch (IOException e) {
            System.err.println("Error updating pie chart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyPieChartColors() {
        // Apply colors after the chart is rendered
        javafx.application.Platform.runLater(() -> {
            for (int i = 0; i < mainPieChart.getData().size(); i++) {
                PieChart.Data data = mainPieChart.getData().get(i);
                if (data.getNode() != null) {
                    String color = PIE_COLORS[i % PIE_COLORS.length];
                    data.getNode().setStyle("-fx-pie-color: " + color + ";");
                }
            }
        });
    }

    private void updateBarChart() {
        try {
            if (popularAttractionsChart == null || barChartTypeComboBox == null) return;

            String chartType = barChartTypeComboBox.getValue();
            if (chartType == null) chartType = "Top Attractions";

            popularAttractionsChart.getData().clear();

            switch (chartType) {
                case "Top Attractions":
                    updateTopAttractionsChart();
                    break;
                case "Monthly Bookings":
                    updateMonthlyBookingsChart();
                    break;
                case "Guide Performance":
                    updateGuidePerformanceChart();
                    break;
            }

            // Enhanced styling for larger bar chart
            popularAttractionsChart.setBarGap(3);
            popularAttractionsChart.setCategoryGap(10);

            // Apply colors to bar chart
            applyBarChartColors();

        } catch (Exception e) {
            System.err.println("Error updating bar chart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyBarChartColors() {
        // Apply colors after the chart is rendered
        javafx.application.Platform.runLater(() -> {
            for (int seriesIndex = 0; seriesIndex < popularAttractionsChart.getData().size(); seriesIndex++) {
                XYChart.Series<String, Number> series = popularAttractionsChart.getData().get(seriesIndex);
                for (int dataIndex = 0; dataIndex < series.getData().size(); dataIndex++) {
                    XYChart.Data<String, Number> data = series.getData().get(dataIndex);
                    if (data.getNode() != null) {
                        String color = BAR_COLORS[dataIndex % BAR_COLORS.length];
                        data.getNode().setStyle("-fx-bar-fill: " + color + ";");
                    }
                }
            }
        });
    }

    private void updateTopAttractionsChart() throws IOException {
        @SuppressWarnings("unchecked")
        List<Booking> bookings = (List<Booking>) analyticsCache.get("bookings");
        if (bookings == null) bookings = bookingDAO.findAll();

        Map<String, Long> attractionBookings = bookings.stream()
                .collect(Collectors.groupingBy(Booking::getAttractionName, Collectors.counting()));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Number of Bookings");

        attractionBookings.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(15)
                .forEach(entry -> {
                    String shortName = entry.getKey().length() > 20 ?
                            entry.getKey().substring(0, 17) + "..." : entry.getKey();
                    series.getData().add(new XYChart.Data<>(shortName, entry.getValue()));
                });

        popularAttractionsChart.getData().add(series);
        barChartXAxis.setLabel("Tourist Attractions");
        barChartYAxis.setLabel("Number of Bookings");
        popularAttractionsChart.setTitle("Top 15 Most Popular Attractions");
    }

    private void updateMonthlyBookingsChart() throws IOException {
        @SuppressWarnings("unchecked")
        List<Booking> bookings = (List<Booking>) analyticsCache.get("bookings");
        if (bookings == null) bookings = bookingDAO.findAll();

        Map<String, Long> monthlyBookings = bookings.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getBookingDate().format(DateTimeFormatter.ofPattern("MMM yyyy")),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Bookings");

        monthlyBookings.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

        popularAttractionsChart.getData().add(series);
        barChartXAxis.setLabel("Month");
        barChartYAxis.setLabel("Number of Bookings");
        popularAttractionsChart.setTitle("Monthly Booking Trends");
    }

    private void updateGuidePerformanceChart() throws IOException {
        @SuppressWarnings("unchecked")
        List<Booking> bookings = (List<Booking>) analyticsCache.get("bookings");
        if (bookings == null) bookings = bookingDAO.findAll();

        Map<String, Long> guideBookings = bookings.stream()
                .filter(b -> b.getGuideName() != null && !b.getGuideName().isEmpty())
                .collect(Collectors.groupingBy(Booking::getGuideName, Collectors.counting()));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Bookings Handled");

        guideBookings.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(12)
                .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

        popularAttractionsChart.getData().add(series);
        barChartXAxis.setLabel("Tour Guides");
        barChartYAxis.setLabel("Number of Bookings");
        popularAttractionsChart.setTitle("Top 12 Guide Performance");
    }

    @FXML
    private void exportCurrentChart() {
        if (mainPieChart != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Pie Chart Data");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));

            Stage stage = (Stage) mainPieChart.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try {
                    exportService.exportPieChartData(mainPieChart, file.getAbsolutePath());
                    showAlert("Success", "Pie chart data exported successfully to: " + file.getName());
                } catch (IOException e) {
                    showAlert("Error", "Failed to export chart data: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void exportBarChart() {
        if (popularAttractionsChart != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Bar Chart Data");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));

            Stage stage = (Stage) popularAttractionsChart.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try {
                    exportBarChartData(file.getAbsolutePath());
                    showAlert("Success", "Bar chart data exported successfully to: " + file.getName());
                } catch (IOException e) {
                    showAlert("Error", "Failed to export bar chart data: " + e.getMessage());
                }
            }
        }
    }

    private void exportBarChartData(String filePath) throws IOException {
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(filePath))) {
            writer.write("Category,Value\n");

            for (XYChart.Series<String, Number> series : popularAttractionsChart.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    writer.write(String.format("%s,%s\n",
                            escapeCSV(data.getXValue()),
                            data.getYValue().toString()));
                }
            }
        }
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
