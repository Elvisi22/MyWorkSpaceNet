package com.travelagency.busbooking.service;

import com.travelagency.busbooking.entity.Bus;
import com.travelagency.busbooking.entity.Trip;
import com.travelagency.busbooking.repository.BusRepository;
import com.travelagency.busbooking.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BusService {

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private TripRepository tripRepository;

    // Fetch all buses
    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    public Bus createBuss(String bussNumber){
        Bus bus = new Bus();
        bus.setName(bussNumber);
        bus.setCapacity(55);
        return  busRepository.save(bus);
    }

    // Get Bus by ID
    public Optional<Bus> getBusById(Long id) {
        return busRepository.findById(id);
    }

    // Create a new Trip based on departure schedule
    public Trip createTrip(Bus bus, LocalDate departureTime, String destination) {
        Trip trip = new Trip();
        trip.setBus(bus);
        trip.setDepartureDate(departureTime);
        trip.setDestination(destination);
        return tripRepository.save(trip);
    }

    // Get trips for a specific week
    public List<Trip> getTripsForWeek(LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(7);
        return tripRepository.findByDepartureDateBetween(weekStart , weekEnd);
    }

    // Automatically generate trips for the next week
    public void generateNextWeekTrips() {
        List<Bus> buses = busRepository.findAll();

        // Create trips for each bus
        for (Bus bus : buses) {
            // Example: Let's assume trips are generated every Friday at 9 AM for Albania to Italy
            LocalDateTime nextFriday = LocalDate.now().atStartOfDay();
            createTrip(bus, LocalDate.from(LocalDate.from(nextFriday).atStartOfDay()), "Italy");

            // Similar logic can be added for other days (Sunday, Monday, etc.)
        }
    }
}
