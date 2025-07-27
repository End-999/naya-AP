package org.example.cleanprj.models;

public class Destination {
    private String name;
    private String location;
    private String duration;
    private String altitude;
    private String description;
    private String price;
    private String imagePath;

    public Destination(String name, String location, String duration, String altitude,
                       String description, String price, String imagePath) {
        this.name = name;
        this.location = location;
        this.duration = duration;
        this.altitude = altitude;
        this.description = description;
        this.price = price;
        this.imagePath = imagePath;
    }

    // Getters
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getDuration() { return duration; }
    public String getAltitude() { return altitude; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public String getImagePath() { return imagePath; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setAltitude(String altitude) { this.altitude = altitude; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(String price) { this.price = price; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
