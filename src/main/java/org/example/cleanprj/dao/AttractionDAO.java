package org.example.cleanprj.dao;

import org.example.cleanprj.models.Attraction;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AttractionDAO {
    private static final String FILE_PATH = "data/attractions.csv";

    public AttractionDAO() {
        createDataDirectoryIfNotExists();
        initializeDefaultData();
    }

    private void createDataDirectoryIfNotExists() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    private void initializeDefaultData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            createDefaultAttractions();
        }
    }

    private void createDefaultAttractions() {
        List<Attraction> defaultAttractions = new ArrayList<>();

        defaultAttractions.add(new Attraction("Mount Everest Base Camp", "Trekking", "Solukhumbu",
                "Extreme", "5364m", "14 days", "March-May, September-November",
                "Trek to the base camp of the world's highest mountain with stunning Himalayan views", 1200.0));

        defaultAttractions.add(new Attraction("Annapurna Circuit", "Trekking", "Annapurna",
                "Hard", "5416m", "21 days", "March-May, October-November",
                "Classic trek around the Annapurna massif through diverse landscapes", 800.0));

        defaultAttractions.add(new Attraction("Chitwan National Park", "Wildlife", "Chitwan",
                "Easy", "150m", "3 days", "October-March",
                "Wildlife safari in Nepal's first national park, home to rhinos and tigers", 300.0));

        defaultAttractions.add(new Attraction("Kathmandu Durbar Square", "Cultural", "Kathmandu",
                "Easy", "1400m", "Half day", "Year round",
                "Historic palace complex in the heart of Kathmandu with ancient temples", 50.0));

        defaultAttractions.add(new Attraction("Pokhara Lake District", "Sightseeing", "Pokhara",
                "Easy", "822m", "2 days", "September-May",
                "Beautiful lake district with mountain reflections and adventure activities", 150.0));

        try {
            for (Attraction attraction : defaultAttractions) {
                save(attraction);
            }
        } catch (IOException e) {
            System.err.println("Error creating default attractions: " + e.getMessage());
        }
    }

    public void save(Attraction attraction) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(attraction.toCSV());
            bw.newLine();
        }
    }

    public List<Attraction> findAll() throws IOException {
        List<Attraction> attractions = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return attractions;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;

                try {
                    Attraction attraction = Attraction.fromCSV(line);
                    if (attraction != null) {
                        attractions.add(attraction);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing line " + lineNumber + ": " + line + " - " + e.getMessage());
                    // Continue processing other lines
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading attractions file: " + e.getMessage());
            throw e;
        }

        return attractions;
    }

    public Attraction findById(String id) throws IOException {
        for (Attraction attraction : findAll()) {
            if (attraction.getId().equals(id)) {
                return attraction;
            }
        }
        return null;
    }

    public void update(Attraction attraction) throws IOException {
        List<Attraction> attractions = findAll();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Attraction a : attractions) {
                if (a.getId().equals(attraction.getId())) {
                    bw.write(attraction.toCSV());
                } else {
                    bw.write(a.toCSV());
                }
                bw.newLine();
            }
        }
    }

    public void delete(String id) throws IOException {
        List<Attraction> attractions = findAll();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Attraction attraction : attractions) {
                if (!attraction.getId().equals(id)) {
                    bw.write(attraction.toCSV());
                    bw.newLine();
                }
            }
        }
    }
}
