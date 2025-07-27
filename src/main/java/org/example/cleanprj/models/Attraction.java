package org.example.cleanprj.models;

import java.util.ArrayList;
import java.util.List;

public class Attraction {
    private String id;
    private String name;
    private String type;
    private String location;
    private String difficulty;
    private String altitude;
    private String duration;
    private String bestSeason;
    private String description;
    private double price;
    private String status;

    // Constructor used by AddNewAttractionController
    public Attraction(String name, String type, String location, String difficulty,
                      String altitude, String duration, String bestSeason, String description, double price) {
        this.id = generateId();
        this.name = name;
        this.type = type;
        this.location = location;
        this.difficulty = difficulty;
        this.altitude = altitude;
        this.duration = duration;
        this.bestSeason = bestSeason;
        this.description = description;
        this.price = price;
        this.status = "Active";
    }

    // Constructor for loading from CSV (with ID)
    public Attraction(String id, String name, String type, String location, String difficulty,
                      String altitude, String duration, String bestSeason, String description,
                      double price, String status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.location = location;
        this.difficulty = difficulty;
        this.altitude = altitude;
        this.duration = duration;
        this.bestSeason = bestSeason;
        this.description = description;
        this.price = price;
        this.status = status != null ? status : "Active";
    }

    // Default constructor
    public Attraction() {
        this.id = generateId();
        this.status = "Active";
    }

    private String generateId() {
        return "ATT_" + System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getAltitude() { return altitude; }
    public void setAltitude(String altitude) { this.altitude = altitude; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getBestSeason() { return bestSeason; }
    public void setBestSeason(String bestSeason) { this.bestSeason = bestSeason; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // CSV conversion methods
    public String toCSV() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%.2f,%s",
                escapeCSV(id),
                escapeCSV(name),
                escapeCSV(type),
                escapeCSV(location),
                escapeCSV(difficulty),
                escapeCSV(altitude),
                escapeCSV(duration),
                escapeCSV(bestSeason),
                escapeCSV(description),
                price,
                escapeCSV(status));
    }

    public static Attraction fromCSV(String csvLine) {
        if (csvLine == null || csvLine.trim().isEmpty()) {
            return null;
        }

        try {
            List<String> fields = splitCSVLine(csvLine);

            if (fields.size() < 11) {
                System.err.println("Invalid CSV line (insufficient fields): " + csvLine);
                return null;
            }

            String id = fields.get(0);
            String name = fields.get(1);
            String type = fields.get(2);
            String location = fields.get(3);
            String difficulty = fields.get(4);
            String altitude = fields.get(5);
            String duration = fields.get(6);
            String bestSeason = fields.get(7);
            String description = fields.get(8);

            // Safe number parsing with default
            double price = parseDoubleWithDefault(fields.get(9), 0.0);
            String status = fields.get(10);

            return new Attraction(id, name, type, location, difficulty, altitude,
                    duration, bestSeason, description, price, status);

        } catch (Exception e) {
            System.err.println("Error parsing CSV line: " + csvLine + " - " + e.getMessage());
            return null;
        }
    }

    private static double parseDoubleWithDefault(String value, double defaultValue) {
        try {
            return value != null && !value.trim().isEmpty() ? Double.parseDouble(value.trim()) : defaultValue;
        } catch (NumberFormatException e) {
            System.err.println("Invalid double value: " + value + ", using default: " + defaultValue);
            return defaultValue;
        }
    }

    private static String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static List<String> splitCSVLine(String line) {
        List<String> fields = new ArrayList<>();
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
        return fields;
    }

    @Override
    public String toString() {
        return name + " (" + location + ")";
    }
}
