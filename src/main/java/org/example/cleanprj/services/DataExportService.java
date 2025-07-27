package org.example.cleanprj.services;

import javafx.scene.chart.PieChart;
import org.example.cleanprj.models.User;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DataExportService {

    public void exportTouristData(List<User> tourists, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Name,Email,Nationality,Registration Date,Emergency Contact\n");

            for (User tourist : tourists) {
                writer.write(String.format("%s,%s,%s,%s,%s\n",
                        escapeCSV(tourist.getFullName()),
                        escapeCSV(tourist.getEmail()),
                        escapeCSV(tourist.getNationality()),
                        tourist.getRegistrationDate().toString(),
                        tourist.getEmergencyContact() != null ? escapeCSV(tourist.getEmergencyContact()) : "N/A"
                ));
            }
        }
    }

    // Alternative method for pie chart export - exports data as CSV instead of image
    public void exportPieChartData(PieChart pieChart, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Category,Value,Percentage\n");

            double total = pieChart.getData().stream()
                    .mapToDouble(PieChart.Data::getPieValue)
                    .sum();

            for (PieChart.Data data : pieChart.getData()) {
                double percentage = (data.getPieValue() / total) * 100;
                writer.write(String.format("%s,%.0f,%.2f%%\n",
                        escapeCSV(data.getName()),
                        data.getPieValue(),
                        percentage
                ));
            }
        }
    }

    // Keep the old method name for compatibility but redirect to data export
    public void exportPieChart(PieChart pieChart, String filePath) throws IOException {
        // Change file extension to .csv if it's .png
        String csvFilePath = filePath.endsWith(".png") ?
                filePath.replace(".png", ".csv") : filePath;
        exportPieChartData(pieChart, csvFilePath);
    }

    public void exportNationalityReport(Map<String, Integer> nationalityData, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Nationality,Count,Percentage\n");

            int total = nationalityData.values().stream().mapToInt(Integer::intValue).sum();

            for (Map.Entry<String, Integer> entry : nationalityData.entrySet()) {
                double percentage = (entry.getValue() * 100.0) / total;
                writer.write(String.format("%s,%d,%.2f%%\n",
                        escapeCSV(entry.getKey()),
                        entry.getValue(),
                        percentage
                ));
            }
        }
    }

    // Export guides data
    public void exportGuideData(List<org.example.cleanprj.models.Guide> guides, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Name,Languages,Experience,Contact,Specialization,Rating,Status\n");

            for (org.example.cleanprj.models.Guide guide : guides) {
                writer.write(String.format("%s,%s,%d,%s,%s,%.1f,%s\n",
                        escapeCSV(guide.getName()),
                        escapeCSV(guide.getLanguages()),
                        guide.getExperience(),
                        escapeCSV(guide.getContact()),
                        escapeCSV(guide.getSpecialization()),
                        guide.getRating(),
                        escapeCSV(guide.getStatus())
                ));
            }
        }
    }

    // Export attractions data
    public void exportAttractionData(List<org.example.cleanprj.models.Attraction> attractions, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Name,Type,Location,Difficulty,Altitude,Duration,Best Season,Price,Status\n");

            for (org.example.cleanprj.models.Attraction attraction : attractions) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%.2f,%s\n",
                        escapeCSV(attraction.getName()),
                        escapeCSV(attraction.getType()),
                        escapeCSV(attraction.getLocation()),
                        escapeCSV(attraction.getDifficulty()),
                        escapeCSV(attraction.getAltitude()),
                        escapeCSV(attraction.getDuration()),
                        escapeCSV(attraction.getBestSeason()),
                        attraction.getPrice(),
                        escapeCSV(attraction.getStatus())
                ));
            }
        }
    }

    // Export bookings data
    public void exportBookingData(List<org.example.cleanprj.models.Booking> bookings, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Tourist Name,Tourist Email,Attraction,Guide,Start Date,End Date,Total Price,Status,Booking Date\n");

            for (org.example.cleanprj.models.Booking booking : bookings) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%.2f,%s,%s\n",
                        escapeCSV(booking.getTouristName()),
                        escapeCSV(booking.getTouristEmail()),
                        escapeCSV(booking.getAttractionName()),
                        booking.getGuideName() != null ? escapeCSV(booking.getGuideName()) : "Not Assigned",
                        booking.getStartDate().toString(),
                        booking.getEndDate().toString(),
                        booking.getTotalPrice(),
                        escapeCSV(booking.getStatus()),
                        booking.getBookingDate().toString()
                ));
            }
        }
    }

    // Export emergency contacts data
    public void exportEmergencyContactData(List<org.example.cleanprj.models.EmergencyContact> contacts, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Name,Organization,Phone,Email,Type,Location,Available 24/7\n");

            for (org.example.cleanprj.models.EmergencyContact contact : contacts) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s\n",
                        escapeCSV(contact.getName()),
                        escapeCSV(contact.getOrganization()),
                        escapeCSV(contact.getPhone()),
                        escapeCSV(contact.getEmail()),
                        escapeCSV(contact.getType()),
                        escapeCSV(contact.getLocation()),
                        contact.isAvailable24_7() ? "Yes" : "No"
                ));
            }
        }
    }

    // Helper method to escape CSV values that contain commas, quotes, or newlines
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

    // Generate summary report
    public void exportSummaryReport(String filePath, int totalTourists, int totalGuides,
                                    int totalAttractions, int totalBookings, double totalRevenue) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Nepal Tourism Management System - Summary Report\n");
            writer.write("Generated on: " + java.time.LocalDateTime.now().toString() + "\n\n");

            writer.write("Statistics:\n");
            writer.write("Total Tourists: " + totalTourists + "\n");
            writer.write("Total Guides: " + totalGuides + "\n");
            writer.write("Total Attractions: " + totalAttractions + "\n");
            writer.write("Total Bookings: " + totalBookings + "\n");
            writer.write("Total Revenue: $" + String.format("%.2f", totalRevenue) + "\n");
        }
    }
}
