package org.example.cleanprj.services;

import org.example.cleanprj.dao.DestinationDAO;
import org.example.cleanprj.models.Destination;
import java.util.List;

public class DestinationService {
    private final DestinationDAO destinationDAO;

    public DestinationService() {
        this.destinationDAO = new DestinationDAO();
    }

    public List<Destination> getAllDestinations() {
        return destinationDAO.findAll();
    }

    public Destination getDestinationByName(String name) {
        return destinationDAO.findByName(name);
    }

    public boolean isHighAltitude(Destination destination) {
        // Extract altitude number and check if > 3000m
        String altitude = destination.getAltitude();
        try {
            int altitudeValue = Integer.parseInt(altitude.replaceAll("[^0-9]", ""));
            return altitudeValue > 3000;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
