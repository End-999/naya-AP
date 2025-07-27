package org.example.cleanprj.dao;

import org.example.cleanprj.models.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static final String FILE_PATH = "data/loginDetails.csv";

    public UserDAO() {
        createDataDirectoryIfNotExists();
        initializeAdminUsers();
    }

    private void createDataDirectoryIfNotExists() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    private void initializeAdminUsers() {
        try {
            // Check if file exists and has admin users
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                // Create file with admin users
                createDefaultAdminUsers();
            } else {
                // Check if admin users exist
                List<User> users = findAll();
                boolean hasAdmin = users.stream().anyMatch(user -> "Admin".equals(user.getRole()));
                if (!hasAdmin) {
                    createDefaultAdminUsers();
                }
            }
        } catch (IOException e) {
            System.err.println("Error initializing admin users: " + e.getMessage());
        }
    }

    private void createDefaultAdminUsers() throws IOException {
        List<User> adminUsers = new ArrayList<>();

        // Create default admin users
        User admin1 = new User("ADMIN_001", "Sahel Admin", "saheladmin123@gmail.com", "sahel@123", "Nepali", "Admin");
        User admin2 = new User("ADMIN_002", "Nupun Admin", "nupunadmin123@gmail.com", "nupun@123", "Nepali", "Admin");
        User admin3 = new User("ADMIN_003", "System Admin", "admin123@gmail.com", "admin@123", "Nepali", "Admin");

        adminUsers.add(admin1);
        adminUsers.add(admin2);
        adminUsers.add(admin3);

        // Write admin users to file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, false))) { // false to overwrite
            for (User admin : adminUsers) {
                bw.write(admin.toCSV());
                bw.newLine();
            }
        }

        System.out.println("Default admin users created successfully!");
    }

    public void save(User user) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(user.toCSV());
            bw.newLine();
        }
        System.out.println("User saved: " + user.getEmail());
    }

    public List<User> findAll() throws IOException {
        List<User> users = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return users; // Return empty list if file doesn't exist
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;

                try {
                    User user = User.fromCSV(line);
                    if (user != null) {
                        users.add(user);
                    } else {
                        System.err.println("Failed to parse user at line " + lineNumber + ": " + line);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing line " + lineNumber + ": " + line + " - " + e.getMessage());
                }
            }
        }

        System.out.println("Loaded " + users.size() + " users from file");
        return users;
    }

    public User findByEmail(String email) throws IOException {
        List<User> users = findAll();
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email.trim())) {
                System.out.println("Found user: " + user.getEmail() + " with role: " + user.getRole());
                return user;
            }
        }
        System.out.println("User not found with email: " + email);
        return null;
    }

    public User findById(String userId) throws IOException {
        List<User> users = findAll();
        for (User user : users) {
            if (user.getId().equals(userId)) {
                System.out.println("Found user by ID: " + user.getEmail() + " with role: " + user.getRole());
                return user;
            }
        }
        System.out.println("User not found with ID: " + userId);
        return null;
    }

    public boolean existsByEmail(String email) throws IOException {
        return findByEmail(email) != null;
    }

    public void update(User user) throws IOException {
        List<User> users = findAll();
        boolean userFound = false;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User u : users) {
                if (u.getId().equals(user.getId())) {
                    bw.write(user.toCSV());
                    userFound = true;
                } else {
                    bw.write(u.toCSV());
                }
                bw.newLine();
            }
        }

        if (userFound) {
            System.out.println("User updated: " + user.getEmail());
        } else {
            System.err.println("User not found for update: " + user.getEmail());
        }
    }

    public boolean delete(String userId) throws IOException {
        List<User> users = findAll();
        boolean userFound = false;
        User deletedUser = null;

        // Find the user to be deleted
        for (User user : users) {
            if (user.getId().equals(userId)) {
                deletedUser = user;
                userFound = true;
                break;
            }
        }

        if (!userFound) {
            System.err.println("User not found for deletion with ID: " + userId);
            return false;
        }

        // Check if trying to delete an admin user
        if ("Admin".equals(deletedUser.getRole())) {
            System.err.println("Cannot delete admin user: " + deletedUser.getEmail());
            throw new IllegalArgumentException("Cannot delete admin users");
        }

        // Remove the user from the list
        users.removeIf(user -> user.getId().equals(userId));

        // Rewrite the file without the deleted user
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User user : users) {
                bw.write(user.toCSV());
                bw.newLine();
            }
        }

        System.out.println("User deleted successfully: " + deletedUser.getEmail());
        return true;
    }

    public boolean deleteByEmail(String email) throws IOException {
        User user = findByEmail(email);
        if (user != null) {
            return delete(user.getId());
        }
        return false;
    }

    // Method to clean up and reformat the CSV file
    public void cleanupCSVFile() throws IOException {
        List<User> users = findAll();

        // Backup the original file
        File originalFile = new File(FILE_PATH);
        File backupFile = new File(FILE_PATH + ".backup");
        if (originalFile.exists()) {
            java.nio.file.Files.copy(originalFile.toPath(), backupFile.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }

        // Rewrite the file with proper formatting
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (User user : users) {
                bw.write(user.toCSV());
                bw.newLine();
            }
        }

        System.out.println("CSV file cleaned up successfully. Backup created at: " + backupFile.getAbsolutePath());
    }

    // Method to get user statistics
    public int getTouristCount() throws IOException {
        return (int) findAll().stream()
                .filter(user -> "Tourist".equals(user.getRole()))
                .count();
    }

    public int getAdminCount() throws IOException {
        return (int) findAll().stream()
                .filter(user -> "Admin".equals(user.getRole()))
                .count();
    }
}
