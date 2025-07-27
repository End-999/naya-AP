package org.example.cleanprj.dao;

import org.example.cleanprj.models.Destination;
import java.util.ArrayList;
import java.util.List;

public class DestinationDAO {

    public List<Destination> findAll() {
        // For now, return hardcoded destinations
        // Later you can implement file-based storage
        List<Destination> destinations = new ArrayList<>();

        destinations.add(new Destination(
                "Kathmandu Durbar Square",
                "Kathmandu",
                "2–3 hours",
                "1400m altitude",
                "Historic palace complex showcasing traditional Nepalese architecture.",
                "$1010",
                "kathmandu.jpg"
        ));

        destinations.add(new Destination(
                "Chitwan National Park",
                "Chitwan",
                "2–3 hours",
                "150m altitude",
                "UNESCO World Heritage site famous for rhinos, tigers, and elephants.",
                "$1600",
                "chitwan.jpg"
        ));

        destinations.add(new Destination(
                "Pokhara Paragliding",
                "Pokhara",
                "2–3 hours",
                "1600m altitude",
                "Tandem paragliding with stunning views of the Himalayas.",
                "$1260",
                "new.jpg"
        ));

        return destinations;
    }

    //    public Destination findByName(String name) {
//        return findAll().stream()
//                .filter(dest -> dest.getName().equalsIgnoreCase(name))
//                .findFirst()
//                .orElse(null);
//    }
    public Destination findByName(String name) {
        for (Destination dest : findAll()) {
            if (dest.getName().equalsIgnoreCase(name)) {
                return dest;
            }
        }
        return null;
    }

}
