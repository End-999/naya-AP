package org.example.cleanprj.dao;

import org.example.cleanprj.models.Booking;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    private static final String FILE_PATH = "data/bookings.csv";

    public BookingDAO() {
        createDataDirectoryIfNotExists();
    }

    private void createDataDirectoryIfNotExists() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    public void save(Booking booking) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(booking.toCSV());
            bw.newLine();
        }
    }

    public List<Booking> findAll() throws IOException {
        List<Booking> bookings = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return bookings;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Booking booking = Booking.fromCSV(line);
                if (booking != null) {
                    bookings.add(booking);
                }
            }
        }
        return bookings;
    }

    public List<Booking> findByTouristId(String touristId) throws IOException {
        List<Booking> userBookings = new ArrayList<>();
        for (Booking booking : findAll()) {
            if (booking.getTouristId().equals(touristId)) {
                userBookings.add(booking);
            }
        }
        return userBookings;
    }

    public Booking findById(String id) throws IOException {
        for (Booking booking : findAll()) {
            if (booking.getId().equals(id)) {
                return booking;
            }
        }
        return null;
    }

    public void update(Booking booking) throws IOException {
        List<Booking> bookings = findAll();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Booking b : bookings) {
                if (b.getId().equals(booking.getId())) {
                    bw.write(booking.toCSV());
                } else {
                    bw.write(b.toCSV());
                }
                bw.newLine();
            }
        }
    }
}
