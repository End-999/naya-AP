package org.example.cleanprj.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class User {
    private String id;
    private String fullName;
    private String email;
    private String password;
    private String nationality;
    private String role;
    private LocalDateTime registrationDate;
    private String emergencyContact;

    public User(String fullName, String email, String password, String nationality) {
        this.id = generateId();
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.nationality = nationality;
        this.role = "Tourist"; // Default role
        this.registrationDate = LocalDateTime.now();
    }

    public User(String id, String fullName, String email, String password, String nationality, String role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.nationality = nationality;
        this.role = role;
        this.registrationDate = LocalDateTime.now();
    }

    // Full constructor for loading from CSV
    public User(String id, String fullName, String email, String password, String nationality,
                String role, LocalDateTime registrationDate, String emergencyContact) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.nationality = nationality;
        this.role = role;
        this.registrationDate = registrationDate;
        this.emergencyContact = emergencyContact;
    }

    private String generateId() {
        return "USER_" + System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String toCSV() {
        return escapeCSV(id) + "," +
                escapeCSV(fullName) + "," +
                escapeCSV(email) + "," +
                escapeCSV(password) + "," +
                escapeCSV(nationality) + "," +
                escapeCSV(role) + "," +
                (registrationDate != null ? registrationDate.toString() : "") + "," +
                escapeCSV(emergencyContact != null ? emergencyContact : "");
    }

    public static User fromCSV(String csvLine) {
        if (csvLine == null || csvLine.trim().isEmpty()) {
            return null;
        }

        try {
            String[] parts = splitCSVLine(csvLine);

            if (parts.length < 6) {
                System.err.println("Invalid CSV line (insufficient fields): " + csvLine);
                return null;
            }

            String id = parts[0].trim();
            String fullName = parts[1].trim();
            String email = parts[2].trim();
            String password = parts[3].trim();
            String nationality = parts[4].trim();
            String role = parts[5].trim();

            // Handle registration date (optional field)
            LocalDateTime registrationDate = LocalDateTime.now();
            if (parts.length > 6 && !parts[6].trim().isEmpty()) {
                try {
                    registrationDate = LocalDateTime.parse(parts[6].trim());
                } catch (Exception e) {
                    System.err.println("Error parsing registration date: " + parts[6] + ", using current time");
                    registrationDate = LocalDateTime.now();
                }
            }

            // Handle emergency contact (optional field)
            String emergencyContact = null;
            if (parts.length > 7 && !parts[7].trim().isEmpty()) {
                emergencyContact = parts[7].trim();
            }

            return new User(id, fullName, email, password, nationality, role, registrationDate, emergencyContact);

        } catch (Exception e) {
            System.err.println("Error parsing CSV line: " + csvLine + " - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Helper method to split CSV line properly handling quoted fields
    private static String[] splitCSVLine(String line) {
        java.util.List<String> fields = new java.util.ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Escaped quote
                    currentField.append('"');
                    i++; // Skip next quote
                } else {
                    // Toggle quote state
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                // Field separator
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }

        // Add the last field
        fields.add(currentField.toString());
        return fields.toArray(new String[0]);
    }

    // Helper method to escape CSV values
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }

        // If the value contains comma, quote, or newline, wrap it in quotes and escape internal quotes
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", nationality='" + nationality + '\'' +
                ", role='" + role + '\'' +
                ", registrationDate=" + registrationDate +
                '}';
    }
}
