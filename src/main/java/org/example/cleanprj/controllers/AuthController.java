package org.example.cleanprj.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.cleanprj.models.User;
import org.example.cleanprj.services.AuthService;
import org.example.cleanprj.utils.SceneChanger;
import org.example.cleanprj.utils.SessionManager;
import org.example.cleanprj.utils.ValidationUtils;

import java.io.IOException;

public class AuthController {

    // Sign Up Form Components
    @FXML private TextField fullNameFieldSignUp;
    @FXML private TextField emailFieldSignUp;
    @FXML private PasswordField passwordFieldSignUp;
    @FXML private PasswordField confirmPasswordFieldSignUp;
    @FXML private TextField nationalityFieldSignUp;
    @FXML private Button signUpButtonAction;
    @FXML private ToggleButton signUpButton;
    @FXML private Label emailErrorLabelSignUp;
    @FXML private Label passwordErrorLabelSignUp;
    @FXML private Label nameErrorLabelSignUp;
    @FXML private Label nationalityErrorLabelSignUp;
    @FXML private VBox signUpForm;

    // Sign In Form Components
    @FXML private ComboBox<String> roleComboBoxSignIn;
    @FXML private TextField emailFieldSignIn;
    @FXML private PasswordField passwordFieldSignIn;
    @FXML private Button signInButtonAction;
    @FXML private ToggleButton signInButton;
    @FXML private VBox signInForm;
    @FXML private Label emailErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private Label credentialsErrorLabel;
    @FXML private Label roleErrorLabel;

    private final AuthService authService;
    private final SceneChanger sceneChanger;

    public AuthController() {
        this.authService = new AuthService();
        this.sceneChanger = new SceneChanger();
    }

    @FXML
    public void initialize() {
        roleComboBoxSignIn.getItems().addAll("Admin", "Tourist");
        signInButton.setOnAction(e -> switchToSignIn());
        signUpButton.setOnAction(e -> switchToSignUp());
        signUpButtonAction.setOnAction(e -> signUp());
        signInButtonAction.setOnAction(e -> signIn());

        // Clean up CSV file on startup if needed
        try {
            authService.cleanupUserData();
        } catch (IOException e) {
            System.err.println("Error cleaning up user data: " + e.getMessage());
        }
    }

    private void switchToSignIn() {
        signInForm.setVisible(true);
        signInForm.setManaged(true);
        signUpForm.setVisible(false);
        signUpForm.setManaged(false);

        signInButton.getStyleClass().remove("inactive-tab");
        signInButton.getStyleClass().add("active-tab");
        signUpButton.getStyleClass().remove("active-tab");
        signUpButton.getStyleClass().add("inactive-tab");
    }

    private void switchToSignUp() {
        signInForm.setVisible(false);
        signInForm.setManaged(false);
        signUpForm.setVisible(true);
        signUpForm.setManaged(true);

        signUpButton.getStyleClass().remove("inactive-tab");
        signUpButton.getStyleClass().add("active-tab");
        signInButton.getStyleClass().remove("active-tab");
        signInButton.getStyleClass().add("inactive-tab");
    }

    private void signUp() {
        clearErrorLabels();

        String name = fullNameFieldSignUp.getText().trim();
        String email = emailFieldSignUp.getText().trim();
        String password = passwordFieldSignUp.getText().trim();
        String confirmPassword = confirmPasswordFieldSignUp.getText().trim();
        String nationality = nationalityFieldSignUp.getText().trim();

        if (!validateSignUpForm(name, email, password, confirmPassword, nationality)) {
            return;
        }

        try {
            User user = new User(name, email, password, nationality);
            authService.registerUser(user);

            // Set session and redirect
            SessionManager.setCurrentUser(user);
            changeScene("tourist-dashboard.fxml");

        } catch (IllegalArgumentException e) {
            emailErrorLabelSignUp.setText("User already exists.");
            emailErrorLabelSignUp.setVisible(true);
        } catch (Exception e) {
            emailErrorLabelSignUp.setText("Error saving user.");
            emailErrorLabelSignUp.setVisible(true);
            e.printStackTrace();
        }
    }

    private boolean validateSignUpForm(String name, String email, String password, String confirmPassword, String nationality) {
        boolean hasError = false;

        if (!ValidationUtils.isNotEmpty(name)) {
            nameErrorLabelSignUp.setText("Name is required!");
            nameErrorLabelSignUp.setVisible(true);
            hasError = true;
        }

        if (!ValidationUtils.isNotEmpty(email)) {
            emailErrorLabelSignUp.setText("Email is required!");
            emailErrorLabelSignUp.setVisible(true);
            hasError = true;
        } else if (!ValidationUtils.isValidEmail(email)) {
            emailErrorLabelSignUp.setText("Invalid email format!");
            emailErrorLabelSignUp.setVisible(true);
            hasError = true;
        }

        if (!ValidationUtils.isNotEmpty(password) || !ValidationUtils.isNotEmpty(confirmPassword)) {
            passwordErrorLabelSignUp.setText("Password is required!");
            passwordErrorLabelSignUp.setVisible(true);
            hasError = true;
        } else if (!password.equals(confirmPassword)) {
            passwordErrorLabelSignUp.setText("Passwords do not match!");
            passwordErrorLabelSignUp.setVisible(true);
            hasError = true;
        } else if (password.length() < 6) {
            passwordErrorLabelSignUp.setText("Password must be at least 6 characters!");
            passwordErrorLabelSignUp.setVisible(true);
            hasError = true;
        }

        if (!ValidationUtils.isNotEmpty(nationality)) {
            nationalityErrorLabelSignUp.setText("Nationality is required!");
            nationalityErrorLabelSignUp.setVisible(true);
            hasError = true;
        }

        return !hasError;
    }

    private void signIn() {
        clearSignInErrors();

        String email = emailFieldSignIn.getText().trim();
        String password = passwordFieldSignIn.getText().trim();
        String role = roleComboBoxSignIn.getValue();

        if (!validateSignInForm(email, password, role)) {
            return;
        }

        try {
            User user = authService.authenticate(email, password, role);

            if (user != null) {
                SessionManager.setCurrentUser(user);

                if ("Admin".equals(user.getRole())) {
                    changeScene("admin-dashboard.fxml");
                } else {
                    changeScene("tourist-dashboard.fxml");
                }
            } else {
                credentialsErrorLabel.setText("Invalid credentials!");
                credentialsErrorLabel.setVisible(true);
            }

        } catch (IllegalArgumentException e) {
            credentialsErrorLabel.setText(e.getMessage());
            credentialsErrorLabel.setVisible(true);
        } catch (Exception e) {
            credentialsErrorLabel.setText("Error during authentication.");
            credentialsErrorLabel.setVisible(true);
            e.printStackTrace();
        }
    }

    private boolean validateSignInForm(String email, String password, String role) {
        boolean hasError = false;

        if (!ValidationUtils.isNotEmpty(email)) {
            emailErrorLabel.setVisible(true);
            hasError = true;
        }

        if (!ValidationUtils.isNotEmpty(password)) {
            passwordErrorLabel.setVisible(true);
            hasError = true;
        }

        if (role == null || role.isEmpty()) {
            roleErrorLabel.setVisible(true);
            hasError = true;
        }

        return !hasError;
    }

    private void clearErrorLabels() {
        nameErrorLabelSignUp.setVisible(false);
        emailErrorLabelSignUp.setVisible(false);
        passwordErrorLabelSignUp.setVisible(false);
        nationalityErrorLabelSignUp.setVisible(false);
    }

    private void clearSignInErrors() {
        emailErrorLabel.setVisible(false);
        passwordErrorLabel.setVisible(false);
        credentialsErrorLabel.setVisible(false);
        roleErrorLabel.setVisible(false);
    }

    private void changeScene(String sceneName) {
        try {
            Stage stage = (Stage) signUpForm.getScene().getWindow();
            sceneChanger.changeScene(sceneName, stage);
        } catch (IOException e) {
            System.err.println("Error changing scene: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
