package org.example.cleanprj.dao;

import org.example.cleanprj.models.Guide;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GuideDAO {
    private static final String FILE_PATH = "data/guides.csv";

    public GuideDAO() {
        createDataDirectoryIfNotExists();
    }

    private void createDataDirectoryIfNotExists() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    public void save(Guide guide) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(guide.toCSV());
            bw.newLine();
        }
    }

    public List<Guide> findAll() throws IOException {
        List<Guide> guides = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return guides;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Guide guide = Guide.fromCSV(line);
                if (guide != null) {
                    guides.add(guide);
                }
            }
        }
        return guides;
    }

    public Guide findById(String id) throws IOException {
        for (Guide guide : findAll()) {
            if (guide.getId().equals(id)) {
                return guide;
            }
        }
        return null;
    }

    public void update(Guide guide) throws IOException {
        List<Guide> guides = findAll();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Guide g : guides) {
                if (g.getId().equals(guide.getId())) {
                    bw.write(guide.toCSV());
                } else {
                    bw.write(g.toCSV());
                }
                bw.newLine();
            }
        }
    }

    public void delete(String id) throws IOException {
        List<Guide> guides = findAll();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Guide guide : guides) {
                if (!guide.getId().equals(id)) {
                    bw.write(guide.toCSV());
                    bw.newLine();
                }
            }
        }
    }
}
