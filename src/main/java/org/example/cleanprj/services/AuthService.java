package org.example.cleanprj.services;

import org.example.cleanprj.dao.UserDAO;
import org.example.cleanprj.models.User;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AuthService {
    private final UserDAO userDAO;
    private static final List<String> ADMIN_EMAILS = Arrays.asList(
            "saheladmin123@gmail.com",
            "nupunadmin123@gmail.com",
            "admin123@gmail.com"
    );

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public User authenticate(String email, String password, String role) throws IOException {
        System.out.println("Attempting to authenticate: " + email + " with role: " + role);

        User user = userDAO.findByEmail(email.trim());

        if (user != null) {
            System.out.println("User found: " + user.getEmail());
            System.out.println("Stored password: " + user.getPassword());
            System.out.println("Provided password: " + password);
            System.out.println("User role: " + user.getRole());

            if (user.getPassword().equals(password.trim())) {
                System.out.println("Password matches!");

                // Check if trying to login as admin
                if ("Admin".equals(role)) {
                    if (ADMIN_EMAILS.contains(email.trim()) || "Admin".equals(user.getRole())) {
                        user.setRole("Admin");
                        System.out.println("Admin login successful");
                        return user;
                    } else {
                        System.out.println("Unauthorized admin access for: " + email);
                        throw new IllegalArgumentException("Unauthorized admin access");
                    }
                } else if ("Tourist".equals(role)) {
                    user.setRole("Tourist");
                    System.out.println("Tourist login successful");
                    return user;
                }
            } else {
                System.out.println("Password does not match!");
            }
        } else {
            System.out.println("User not found with email: " + email);
        }

        return null; // Authentication failed
    }

    public User registerUser(User user) throws IOException {
        System.out.println("Attempting to register user: " + user.getEmail());

        if (userDAO.existsByEmail(user.getEmail())) {
            System.out.println("Email already registered: " + user.getEmail());
            throw new IllegalArgumentException("Email already registered");
        }

        userDAO.save(user);
        System.out.println("User registered successfully: " + user.getEmail());
        return user;
    }

    public boolean isAdminEmail(String email) {
        return ADMIN_EMAILS.contains(email.trim());
    }

    // Method to clean up the CSV file if needed
    public void cleanupUserData() throws IOException {
        userDAO.cleanupCSVFile();
    }
}
