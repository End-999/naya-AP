package org.example.cleanprj.models;

public class Guide {
    private String id;
    private String name;
    private String languages;
    private int experience;
    private String contact;
    private String specialization;
    private double rating;
    private String status;

    public Guide(String name, String languages, int experience, String contact, String specialization) {
        this.id = generateId();
        this.name = name;
        this.languages = languages;
        this.experience = experience;
        this.contact = contact;
        this.specialization = specialization;
        this.rating = 0.0;
        this.status = "Active";
    }

    private String generateId() {
        return "GUIDE_" + System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLanguages() { return languages; }
    public void setLanguages(String languages) { this.languages = languages; }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String toCSV() {
        return id + "," + name + "," + languages + "," + experience + "," + contact + "," + specialization + "," + rating + "," + status;
    }

    public static Guide fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length >= 6) {
            Guide guide = new Guide(parts[1], parts[2], Integer.parseInt(parts[3]), parts[4], parts[5]);
            guide.setId(parts[0]);
            if (parts.length > 6) guide.setRating(Double.parseDouble(parts[6]));
            if (parts.length > 7) guide.setStatus(parts[7]);
            return guide;
        }
        return null;
    }
}
