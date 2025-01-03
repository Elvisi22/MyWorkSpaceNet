package com.travelagency.busbooking.service;

import com.travelagency.busbooking.entity.Bus;
import com.travelagency.busbooking.entity.Ticket;
import com.travelagency.busbooking.entity.Trip;
import com.travelagency.busbooking.entity.User;
import com.travelagency.busbooking.repository.TicketRepository;
import com.travelagency.busbooking.repository.TripRepository;
import com.travelagency.busbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TripService tripService;
    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    // Book a ticket for a given trip
//    public String bookTicket(String passengerName , String nr_pashaporte , Long employeeId, Long tripId) {
//        // Fetch the Trip object by ID, or throw an exception if not found
//        Trip trip = tripRepository.findById(tripId)
//                .orElseThrow(() -> new RuntimeException("Trip not found"));
//
//        Bus bus = trip.getBus(); // Assuming there's a relationship between Trip and Bus
//
//        // Check if the bus is full
//        if (trip.getBookedTickets().size() >= bus.getCapacity()) {
//            throw new RuntimeException("Bus is full. Cannot book ticket.");
//        }
//
//        // Find the employee by ID
//        User employee = userRepository.findById(employeeId)
//                .orElseThrow(() -> new RuntimeException("Employee not found"));
//
//        // Create and set up the Ticket object
//        Ticket ticket = new Ticket();
//        ticket.setBookingTime(LocalDate.now());
//        ticket.setPassengerName(passengerName); // Set the passenger's name
//        ticket.setNr_pashaporte(nr_pashaporte);
//        ticket.setEmployee(employee); // Set the employee object
//        ticket.setTrip(trip); // Associate the ticket with the trip
//
//        // Add the ticket to the trip's booked tickets (optional depending on your implementation)
//        trip.addTicket(ticket);
//
//        // Save the ticket in the repository
//        ticketRepository.save(ticket);
//
//        return "Ticket booked successfully"; // Return a success message
//    }
//

    public Ticket bookTicket(String passengerName, String nr_pashaporte, Long employeeId, Long tripId, BigDecimal totalPrice) {
        // Fetch the Trip object by ID, or throw an exception if not found
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        Bus bus = trip.getBus(); // Assuming there's a relationship between Trip and Bus

        // Check if the bus is full
        if (trip.getBookedTickets().size() >= bus.getCapacity()) {
            throw new RuntimeException("Bus is full. Cannot book ticket.");
        }

        // Find the employee by ID
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Create and set up the Ticket object
        Ticket ticket = new Ticket();
        ticket.setBookingTime(LocalDate.now());
        ticket.setPassengerName(passengerName); // Set the passenger's name
        ticket.setNr_pashaporte(nr_pashaporte);
        ticket.setEmployee(employee); // Set the employee object
        ticket.setTrip(trip); // Associate the ticket with the trip
        ticket.setPrice(totalPrice); // Set the total price

        // Add the ticket to the trip's booked tickets (optional depending on your implementation)
        trip.addTicket(ticket);

        // Save the ticket in the repository
        ticketRepository.save(ticket);

//        return "Ticket booked successfully"; // Return a success message
        return ticket;
    }




    // Fetch all tickets for a given trip
    public List<Ticket> getTicketsForTrip(Long tripId) {
        Optional<Trip> optionalTrip = tripService.getTripById(tripId);
        if (optionalTrip.isEmpty()) {
            throw new RuntimeException("Trip not found.");
        }

        Trip trip = optionalTrip.get();
        return trip.getTickets();
    }


    public List<Ticket> getAllTickets(){
        List<Ticket> tickets = ticketRepository.findAll();
        tickets.sort(Comparator.comparing(Ticket::getId).reversed());
        return tickets;
    }

    public String deleteTicket(Long id){
        ticketRepository.deleteById(id);
        return "Successfully deleted";
    }

    public List<Ticket> getTicketsByEmployee(User employee) {

        List<Ticket> tickets = ticketRepository.findByEmployee(employee); // Get tickets for the employee
        // Sort tickets by booking time in descending order (newest first)
        tickets.sort(Comparator.comparing(Ticket::getId).reversed());
        return tickets;
    }

    public BigDecimal calculatePrice(int adults, int children, int infants) {
        BigDecimal adultPrice = BigDecimal.valueOf(80);  // standard adult price
        BigDecimal childPrice = adultPrice.multiply(BigDecimal.valueOf(0.7)); // 70% of adult price
        BigDecimal infantPrice = adultPrice.multiply(BigDecimal.valueOf(0.5)); // 50% of adult price

        BigDecimal adultTotal = adultPrice.multiply(BigDecimal.valueOf(adults));
        BigDecimal childTotal = childPrice.multiply(BigDecimal.valueOf(children));
        BigDecimal infantTotal = infantPrice.multiply(BigDecimal.valueOf(infants));

        return adultTotal.add(childTotal).add(infantTotal);
    }

    public Ticket findTicketById(Long id){
       return ticketRepository.findTicketById(id);
    }


    public List<Ticket> getAllTicketForTheTrip(Long id){
        List<Ticket> tickets = ticketRepository.findTicketByTripId(id);
        tickets.sort(Comparator.comparing(Ticket::getId).reversed());
        return tickets;
    }

    public List<Ticket> findTicketsByDateRange(LocalDate startDate, LocalDate endDate) {
        return ticketRepository.findByBookingTimeBetween(startDate, endDate);
    }


}

