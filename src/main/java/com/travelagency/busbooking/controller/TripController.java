package com.travelagency.busbooking.controller;

import com.travelagency.busbooking.entity.Bus;
import com.travelagency.busbooking.entity.Trip;
import com.travelagency.busbooking.service.BusService;
import com.travelagency.busbooking.service.TicketService;
import com.travelagency.busbooking.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private BusService busService;

    // Display all trips
    @GetMapping
    public String viewAllTrips(Model model) {
        List<Trip> trips = tripService.getActiveTrips();
        model.addAttribute("trips", trips);
        return "trips"; // The Thymeleaf view for showing all trips
    }

    @GetMapping("allTrips")
    public String lookAllTrips(Model model){
        List<Trip> trips= tripService.getAllTrips();
        model.addAttribute("trips" , trips);
        return "udhetimeAktive";
    }

    // Display a form to create a new trip
//    @GetMapping("/new")
//    public String showTripForm(Model model) {
//        model.addAttribute("trip", new Trip());
//        model.addAttribute("buses", busService.getAllBuses());
//        return "trip-form"; // The Thymeleaf view for trip creation
//    }

    @GetMapping("/create")
    public String showTripForm(Model model) {
        // Add an empty trip object for binding the form data
        Trip trip = new Trip();
        model.addAttribute("trip", trip);

        // Add buses to the model to populate the dropdown
        model.addAttribute("buses", busService.getAllBuses());

        // Return the view name of the form
        return "tripForm"; // This corresponds to tripsForm.html in the templates folder
    }

    // Handle creating a new trip
    @PostMapping("/create")
    public String createTrip(@ModelAttribute("trip") Trip trip, @RequestParam("busId") Long busId, Model model) {
        Optional<Bus> busOpt = busService.getBusById(busId);
        if (busOpt.isPresent()) {
            trip.setBus(busOpt.get());
            tripService.saveTrip(trip);
            return "redirect:/trips"; // Redirect to the trips list after successful creation
        } else {
            model.addAttribute("error", "Bus not found!");
            model.addAttribute("buses", busService.getAllBuses());
            return "trip-form"; // Stay on the form and display the error
        }
    }

    // Display a specific trip
    @GetMapping("/{id}")
    public String viewTrip(@PathVariable Long id, Model model) {
        Optional<Optional<Trip>> tripOpt = Optional.ofNullable(tripService.getTripById(id));
        if (tripOpt.isPresent()) {
            model.addAttribute("trips", tripOpt.get());
            return "trip-detail"; // The Thymeleaf view for showing trip details
        } else {
            model.addAttribute("error", "Trip not found!");
            return "redirect:/trips";
        }
    }

    // Handle trip deletion
    @PostMapping("/{id}/delete")
    public String deleteTrip(@PathVariable Long id) {
        tripService.deleteTripById(id);
        return "redirect:/trips/allTrips"; // Redirect after deletion
    }




//    @GetMapping("/searchTrips")
//    public String searchTrips(@RequestParam("origin") String origin,
//                              @RequestParam("destination") String destination,
//                              @RequestParam("adults") int adults,
//                              @RequestParam("children") int children,
//                              @RequestParam("infants") int infants,
//                              Model model) {
//        // Define ticket prices
//        double adultPrice = 80.0;
//        double childDiscount = 0.70;  // 30% discount for children
//        double infantDiscount = 0.50; // 50% discount for infants
//
//        // Calculate total price
//        double totalPrice = (adults * adultPrice) + (children * adultPrice * childDiscount) + (infants * adultPrice * infantDiscount);
//        // Log the origin and destination inputs
//        System.out.println("Received search request:");
//        System.out.println("Origin: " + origin);
//        System.out.println("Destination: " + destination);
//
//        // Perform the search logic using origin and destination
//        List<Trip> trips = tripService.searchTripsByOriginAndDestination(origin, destination);
//
//        // Log the result of the search (if trips are found or not)
//        if (trips != null && !trips.isEmpty()) {
//            System.out.println("Trips found: " + trips.size());
//            trips.forEach(trip -> System.out.println("Trip ID: " + trip.getId() + ", Origin: " + trip.getOrigin() + ", Destination: " + trip.getDestination()));
//        } else {
//            System.out.println("No trips found for the given origin and destination.");
//        }
//
//        // Add the search results to the model
//        model.addAttribute("trips", trips);
//        model.addAttribute("totalPrice", totalPrice);
//
//        // Log the model attribute to check if trips are added correctly
//        System.out.println("Trips added to model: " + model.containsAttribute("trips"));
//
//        // Return the view that displays the available trips
//        return "trips"; // Make sure "trips.html" exists in your templates folder
//    }

    @GetMapping("/searchTrips")
    public String searchTrips(@RequestParam("origin") String origin,
                              @RequestParam("destination") String destination,
                              @RequestParam("adults") int adults,
                              @RequestParam("children") int children,
                              @RequestParam("infants") int infants,
                              Model model) {

        // Calculate total price using the backend calculation method
        BigDecimal totalPrice = ticketService.calculatePrice(adults, children, infants);

        // Log the origin and destination inputs
        System.out.println("Received search request:");
        System.out.println("Origin: " + origin);
        System.out.println("Destination: " + destination);

        // Perform the search logic using origin and destination
        List<Trip> trips = tripService.searchTripsByOriginAndDestination(origin, destination);

        // Log the result of the search (if trips are found or not)
        if (trips != null && !trips.isEmpty()) {
            System.out.println("Trips found: " + trips.size());
            trips.forEach(trip -> System.out.println("Trip ID: " + trip.getId() + ", Origin: " + trip.getOrigin() + ", Destination: " + trip.getDestination()));
        } else {
            System.out.println("No trips found for the given origin and destination.");
        }

        // Add the search results and calculated price to the model
        if (trips.isEmpty()) {
            model.addAttribute("noTripsMessage", "Nuk ka asnje udhetim available.");
        } else {
            model.addAttribute("trips", trips);
        }
        model.addAttribute("totalPrice", totalPrice);


        // Log the model attribute to check if trips are added correctly
        System.out.println("Trips added to model: " + model.containsAttribute("trips"));

        // Return the view that displays the available trips
        return "trips"; // Make sure "trips.html" exists in your templates folder
    }


    @PostMapping("/admin/uploadTrips")
    public String uploadTrips(@RequestParam("file") MultipartFile file, Model model) {
        try {
            // Parse the file and save trips
            tripService.saveTripsFromExcel(file);
            model.addAttribute("successMessage", "Trips uploaded successfully!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to upload trips: " + e.getMessage());
        }
        return "redirect:/trips"; // Redirect to the admin trip page
    }



}

