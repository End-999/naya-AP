package org.example.cleanprj.services;

import org.example.cleanprj.dao.BookingDAO;
import org.example.cleanprj.models.Booking;
import org.example.cleanprj.models.User;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookingService {
    private final BookingDAO bookingDAO;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
    }

    public Booking createBooking(String touristId, String touristName, String touristEmail,
                                 String touristContact, String attractionId, String attractionName,
                                 LocalDate startDate, LocalDate endDate, String notes, double totalPrice) throws IOException {

        Booking booking = new Booking(touristId, touristName, touristEmail, touristContact,
                attractionId, attractionName, startDate, endDate, notes, totalPrice);

        bookingDAO.save(booking);
        return booking;
    }

    public List<Booking> getBookingsByTourist(String touristId) throws IOException {
        return bookingDAO.findByTouristId(touristId);
    }

    public List<Booking> getAllBookings() throws IOException {
        return bookingDAO.findAll();
    }

    public void updateBookingStatus(String bookingId, String status) throws IOException {
        Booking booking = bookingDAO.findById(bookingId);
        if (booking != null) {
            booking.setStatus(status);
            bookingDAO.update(booking);
        }
    }

    public void assignGuideToBooking(String bookingId, String guideId, String guideName) throws IOException {
        Booking booking = bookingDAO.findById(bookingId);
        if (booking != null) {
            booking.assignGuide(guideId, guideName);
            booking.setStatus("Confirmed"); // Update status when guide is assigned
            bookingDAO.update(booking);
        }
    }

    public List<Booking> getBookingsByStatus(String status) throws IOException {
        return getAllBookings().stream()
                .filter(booking -> booking.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    public List<Booking> getPendingBookings() throws IOException {
        return getBookingsByStatus("Pending");
    }

    public List<Booking> getConfirmedBookings() throws IOException {
        return getBookingsByStatus("Confirmed");
    }

    public double getTotalRevenue() throws IOException {
        return getAllBookings().stream()
                .filter(booking -> !"Cancelled".equalsIgnoreCase(booking.getStatus()))
                .mapToDouble(Booking::getTotalPrice)
                .sum();
    }

    public int getTotalBookingsCount() throws IOException {
        return getAllBookings().size();
    }

    public void cancelBooking(String bookingId) throws IOException {
        Booking booking = bookingDAO.findById(bookingId);
        if (booking != null) {
            booking.setStatus("Cancelled");
            bookingDAO.update(booking);
        } else {
            throw new IllegalArgumentException("Booking not found with ID: " + bookingId);
        }
    }

    public boolean canCancelBooking(Booking booking) {
        if (booking == null) return false;
        String status = booking.getStatus().toLowerCase();
        return status.equals("pending") || status.equals("confirmed");
    }

    public List<Booking> getBookingsByStatusAndTourist(String status, String touristId) throws IOException {
        return getBookingsByTourist(touristId).stream()
                .filter(booking -> booking.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    public void completeBooking(String bookingId) throws IOException {
        updateBookingStatus(bookingId, "Completed");
    }
}
