package org.example.cleanprj.dao;

import org.example.cleanprj.models.EmergencyContact;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EmergencyContactDAO {
    private static final String FILE_PATH = "data/emergency_contacts.csv";

    public EmergencyContactDAO() {
        createDataDirectoryIfNotExists();
        initializeDefaultContacts();
    }

    private void createDataDirectoryIfNotExists() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    private void initializeDefaultContacts() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                List<EmergencyContact> defaultContacts = new ArrayList<>();
                defaultContacts.add(new EmergencyContact("Nepal Police", "Nepal Police", "100",
                        "info@nepalpolice.gov.np", "Police", "Nationwide", true));
                defaultContacts.add(new EmergencyContact("Tourist Police", "Nepal Tourism Board",
                        "+977-1-4247041", "info@ntb.gov.np", "Tourist Police", "Kathmandu", true));
                defaultContacts.add(new EmergencyContact("Himalayan Rescue Association", "HRA",
                        "+977-1-4440292", "hra@hra.org.np", "Mountain Rescue", "Kathmandu", false));

                try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
                    for (EmergencyContact contact : defaultContacts) {
                        bw.write(contact.toCSV());
                        bw.newLine();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save(EmergencyContact contact) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(contact.toCSV());
            bw.newLine();
        }
    }

    public List<EmergencyContact> findAll() throws IOException {
        List<EmergencyContact> contacts = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return contacts;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                EmergencyContact contact = EmergencyContact.fromCSV(line);
                if (contact != null) {
                    contacts.add(contact);
                }
            }
        }
        return contacts;
    }
}
