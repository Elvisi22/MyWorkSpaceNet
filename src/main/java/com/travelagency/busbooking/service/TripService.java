package com.travelagency.busbooking.service;

import com.travelagency.busbooking.entity.Bus;
import com.travelagency.busbooking.entity.Ticket;
import com.travelagency.busbooking.entity.Trip;
import com.travelagency.busbooking.repository.BusRepository;
import com.travelagency.busbooking.repository.TripRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private BusService busService;  // BusService handles bus logic

    @Autowired
    private BusRepository busRepository;

    // Generate weekly trips based on the schedule


    // Create a new trip
    public Trip createTrip(Bus bus, LocalDate departureDate, String destination, String origin ,  String departureTime) {
        Trip trip = new Trip();
        trip.setBus(bus);
        trip.setDepartureDate(departureDate);
        trip.setDepartureTime(departureTime);
        trip.setDestination(destination);
        trip.setOrigin(origin);
        trip.setActive(true);
        return tripRepository.save(trip);
    }

    // Check if a trip already exists for the next week
    private boolean tripExistsForNextWeek(Bus bus, DayOfWeek dayOfWeek) {
        LocalDate nextWeekDate = LocalDate.now().plusWeeks(1).with(dayOfWeek);
        return tripRepository.findByDepartureDateAndBusId(LocalDate.from(nextWeekDate.atStartOfDay()), bus.getId()) != null;
    }

    // Retrieve active trips (for booking)
    public List<Trip> getActiveTrips() {
        return tripRepository.findAll();  // Add logic to filter active trips
    }


    public Optional<Trip> getTripById(Long id) {
        return tripRepository.findById(id);
    }
    public List<Trip> getAllTrips() {
        List<Trip> trips = tripRepository.findAll();
        trips.sort(Comparator.comparing(Trip::getId).reversed());
        return trips;

    }

    // Find trip by ID
    public Optional<Trip> findById(Long id) {
        return tripRepository.findById(id);
    }

    // Save a trip (create or update)
    public Trip saveTrip(Trip trip) {
        return tripRepository.save(trip);
    }

    // Delete a trip by ID
    public void deleteTripById(Long id) {
        tripRepository.deleteById(id);
    }


    public List<Trip> findTripsByDate(LocalDate date) {
        return tripRepository.findByDepartureDate(date); // You can write a query to find trips by date
    }

    public List<Trip> searchTripsByOriginAndDestination(String origin, String destination) {
        LocalDateTime now = LocalDateTime.now(); // Current date and time

        List<Trip> trips = tripRepository.findByOriginAndDestination(origin, destination);

        return trips.stream()
                .filter(trip -> {
                    LocalDate tripDate = trip.getDepartureDate();
                    LocalTime tripTime = parseTime(trip.getDepartureTime());

                    // Check if time parsing failed
                    if (tripTime == null) {
                        System.out.println("Failed to parse time for trip: " + trip);
                        return false; // Skip this trip
                    }

                    LocalDateTime tripDateTime = LocalDateTime.of(tripDate, tripTime);

                    // Debugging statement
                    System.out.println("Checking trip: " + tripDateTime + " against current time: " + now);

                    // Only include future trips
                    return tripDateTime.isAfter(now);
                })
                .collect(Collectors.toList());
    }

    public LocalTime parseTime(String time) {
        // Normalize the time input to include a space before AM/PM
        String formattedTime = time.trim().toUpperCase();
        formattedTime = formattedTime.replaceAll("(\\d+)([APM]{2})", "$1 $2"); // Ensure space before AM/PM

        // Using a 12-hour format with minutes and AM/PM
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a"); // For 1-12 hour format with minutes
            return LocalTime.parse(formattedTime, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing time: " + e.getMessage());
            return null; // Return null if parsing fails
        }
    }

    public void saveTripsFromExcel(MultipartFile file) throws Exception {
        System.out.println("Starting to process the Excel file...");
        List<Trip> trips = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Assume data is in the first sheet
            System.out.println("Reading data from the first sheet...");

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    System.out.println("Skipping header row...");
                    continue; // Skip header row
                }

                try {
                    // Read data from the Excel row
                    String busNameOrId = row.getCell(0).getStringCellValue().trim(); // Bus name or ID
                    LocalDate departureDate = row.getCell(1).getLocalDateTimeCellValue().toLocalDate();
                    String origin = row.getCell(2).getStringCellValue().trim();
                    String destination = row.getCell(3).getStringCellValue().trim();
                    String departureTime = row.getCell(4).getStringCellValue().trim();

                    System.out.println("Processing row: " + row.getRowNum());
                    System.out.println("Bus Name/ID: " + busNameOrId + ", Date: " + departureDate + ", Origin: " + origin +
                            ", Destination: " + destination + ", Time: " + departureTime);

                    // Fetch Bus object from the database
                    Bus bus = busRepository.findByNameOrId(busNameOrId)
                            .orElseThrow(() -> new IllegalArgumentException("Bus not found: " + busNameOrId));

                    System.out.println("Bus found: " + bus);

                    // Create a Trip object
                    Trip trip = new Trip();
                    trip.setBus(bus); // Set the Bus object
                    trip.setDepartureDate(departureDate);
                    trip.setOrigin(origin);
                    trip.setDestination(destination);
                    trip.setDepartureTime(departureTime);

                    trips.add(trip);
                } catch (Exception e) {
                    System.err.println("Error processing row " + row.getRowNum() + ": " + e.getMessage());
                }
            }

            // Save all trips to the database
            tripRepository.saveAll(trips);
            System.out.println("All trips saved successfully. Total trips: " + trips.size());

        } catch (Exception e) {
            System.err.println("Error processing the Excel file: " + e.getMessage());
            throw e; // Rethrow exception for further handling
        }
    }




}
