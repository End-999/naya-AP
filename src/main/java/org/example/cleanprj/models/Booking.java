package org.example.cleanprj.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Booking {
    private String id;
    private String touristId;
    private String touristName;
    private String touristEmail;
    private String touristContact;
    private String attractionId;
    private String attractionName;
    private String guideId;
    private String guideName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String notes;
    private double totalPrice;
    private String status;
    private LocalDateTime bookingDate;

    // Main constructor used by BookingsDialogController
    public Booking(String touristId, String touristName, String touristEmail, String touristContact,
                   String attractionId, String attractionName, LocalDate startDate, LocalDate endDate,
                   String notes, double totalPrice) {
        this.id = generateId();
        this.touristId = touristId;
        this.touristName = touristName;
        this.touristEmail = touristEmail;
        this.touristContact = touristContact;
        this.attractionId = attractionId;
        this.attractionName = attractionName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
        this.totalPrice = totalPrice;
        this.status = "Pending";
        this.bookingDate = LocalDateTime.now();
        this.guideId = null;
        this.guideName = null;
    }

    // Constructor for loading from CSV
    public Booking(String id, String touristId, String touristName, String touristEmail, String touristContact,
                   String attractionId, String attractionName, String guideId, String guideName,
                   LocalDate startDate, LocalDate endDate, String notes, double totalPrice,
                   String status, LocalDateTime bookingDate) {
        this.id = id;
        this.touristId = touristId;
        this.touristName = touristName;
        this.touristEmail = touristEmail;
        this.touristContact = touristContact;
        this.attractionId = attractionId;
        this.attractionName = attractionName;
        this.guideId = guideId;
        this.guideName = guideName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notes = notes;
        this.totalPrice = totalPrice;
        this.status = status;
        this.bookingDate = bookingDate;
    }

    private String generateId() {
        return "BOOK_" + System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTouristId() { return touristId; }
    public void setTouristId(String touristId) { this.touristId = touristId; }

    public String getTouristName() { return touristName; }
    public void setTouristName(String touristName) { this.touristName = touristName; }

    public String getTouristEmail() { return touristEmail; }
    public void setTouristEmail(String touristEmail) { this.touristEmail = touristEmail; }

    public String getTouristContact() { return touristContact; }
    public void setTouristContact(String touristContact) { this.touristContact = touristContact; }

    public String getAttractionId() { return attractionId; }
    public void setAttractionId(String attractionId) { this.attractionId = attractionId; }

    public String getAttractionName() { return attractionName; }
    public void setAttractionName(String attractionName) { this.attractionName = attractionName; }

    public String getGuideId() { return guideId; }
    public void setGuideId(String guideId) { this.guideId = guideId; }

    public String getGuideName() { return guideName; }
    public void setGuideName(String guideName) { this.guideName = guideName; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }

    // Method to assign guide to booking
    public void assignGuide(String guideId, String guideName) {
        this.guideId = guideId;
        this.guideName = guideName;
    }

    public String toCSV() {
        return id + "," + touristId + "," + touristName + "," + touristEmail + "," + touristContact + "," +
                attractionId + "," + attractionName + "," + (guideId != null ? guideId : "") + "," +
                (guideName != null ? guideName : "") + "," + startDate + "," + endDate + "," +
                (notes != null ? notes.replace(",", ";") : "") + "," + totalPrice + "," + status + "," + bookingDate;
    }

    public static Booking fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length >= 14) {
            try {
                String id = parts[0];
                String touristId = parts[1];
                String touristName = parts[2];
                String touristEmail = parts[3];
                String touristContact = parts[4];
                String attractionId = parts[5];
                String attractionName = parts[6];
                String guideId = parts[7].isEmpty() ? null : parts[7];
                String guideName = parts[8].isEmpty() ? null : parts[8];
                LocalDate startDate = LocalDate.parse(parts[9]);
                LocalDate endDate = LocalDate.parse(parts[10]);
                String notes = parts[11].isEmpty() ? null : parts[11].replace(";", ",");
                double totalPrice = Double.parseDouble(parts[12]);
                String status = parts[13];
                LocalDateTime bookingDate = parts.length > 14 ? LocalDateTime.parse(parts[14]) : LocalDateTime.now();

                return new Booking(id, touristId, touristName, touristEmail, touristContact,
                        attractionId, attractionName, guideId, guideName, startDate, endDate,
                        notes, totalPrice, status, bookingDate);
            } catch (Exception e) {
                System.err.println("Error parsing booking CSV line: " + csvLine);
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
