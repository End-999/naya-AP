package org.example.cleanprj.models;

public class EmergencyContact {
    private String id;
    private String name;
    private String organization;
    private String phone;
    private String email;
    private String type;
    private String location;
    private boolean available24_7;

    public EmergencyContact(String name, String organization, String phone, String email,
                            String type, String location, boolean available24_7) {
        this.id = generateId();
        this.name = name;
        this.organization = organization;
        this.phone = phone;
        this.email = email;
        this.type = type;
        this.location = location;
        this.available24_7 = available24_7;
    }

    private String generateId() {
        return "EMRG_" + System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isAvailable24_7() { return available24_7; }
    public void setAvailable24_7(boolean available24_7) { this.available24_7 = available24_7; }

    public String toCSV() {
        return id + "," + name + "," + organization + "," + phone + "," + email + "," +
                type + "," + location + "," + available24_7;
    }

    public static EmergencyContact fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length >= 8) {
            EmergencyContact contact = new EmergencyContact(parts[1], parts[2], parts[3], parts[4],
                    parts[5], parts[6], Boolean.parseBoolean(parts[7]));
            contact.setId(parts[0]);
            return contact;
        }
        return null;
    }
}
