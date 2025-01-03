package com.travelagency.busbooking.controller;

import com.travelagency.busbooking.entity.Ticket;
import com.travelagency.busbooking.entity.Trip;
import com.travelagency.busbooking.entity.User;
import com.travelagency.busbooking.repository.TripRepository;
import com.travelagency.busbooking.repository.UserRepository;
import com.travelagency.busbooking.service.PdfService;
import com.travelagency.busbooking.service.TicketService;
import com.travelagency.busbooking.service.TripService;
import com.travelagency.busbooking.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Controller
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TripService tripService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private PdfService pdfService;

    @GetMapping("/bookTicket/{tripId}")
    public String showBookingForm(@PathVariable Long tripId,@RequestParam BigDecimal totalPrice, Model model) {
        // Fetch the Trip object
        Trip trip = tripService.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid trip ID: " + tripId));
        model.addAttribute("trip", trip); // Add the Trip object to the model
        model.addAttribute("displayTotalPrice" , totalPrice);
        return "bookTicket"; // Render the bookTicket page
    }



//    @PostMapping("/bookTicket")
//    public String bookTicket(@RequestParam("tripId") Long tripId,
//                             @RequestParam("passengerName") String passengerName,
//                             @RequestParam("nr_pashaporte") String nr_pashaporte,
//                             Model model , Principal principal) {
//        // Fetch the trip using the tripId
//        Optional<Trip> optionalTrip = tripService.getTripById(tripId);
//        String username = principal.getName(); // Username of logged-in employee
//        User employee = userService.findUserByEmail(username);
//
//        if (optionalTrip.isPresent()) {
//            Trip trip = optionalTrip.get();
//
//            // Create and save the ticket
//            Ticket ticket = new Ticket();
//            ticket.setPassengerName(passengerName);
//            ticket.setNr_pashaporte(nr_pashaporte);
//            ticket.setTrip(trip); // Set the Trip object
//
//            ticketService.bookTicket((ticket.getPassengerName()), ticket.getNr_pashaporte() , employee.getId(), ticket.getTrip().getId());
//
//            // Success handling
//            model.addAttribute("message", "Ticket booked successfully!");
//            return "myTickets";
//        } else {
//            // Handle case when the Trip is not found
//            model.addAttribute("error", "Trip not found!");
//            return "errorPage";
//        }
//
//
//    }

    @PostMapping("/bookTicket")
    public String bookTicket(@RequestParam("tripId") Long tripId,
                             @RequestParam("totalPrice") BigDecimal totalPrice,
                             @RequestParam("passengerName") String passengerName,
                             @RequestParam("nr_pashaporte") String nr_pashaporte,
                             Principal principal,
                             Model model) {
        Optional<Trip> optionalTrip = tripService.getTripById(tripId);
        String username = principal.getName();
        User employee = userService.findUserByEmail(username);

        if (optionalTrip.isPresent()) {
            Trip trip = optionalTrip.get();

            // Create the Ticket object
            Ticket ticket = new Ticket();
            ticket.setTrip(trip);
            ticket.setPassengerName(passengerName);
            ticket.setNr_pashaporte(nr_pashaporte);
            ticket.setPrice(totalPrice); // Set the price from the form

            // Book the ticket
            Ticket savedTicket = ticketService.bookTicket(ticket.getPassengerName(), ticket.getNr_pashaporte(), employee.getId(), ticket.getTrip().getId(), ticket.getPrice());

            // Redirect to the PDF download endpoint with the ticket ID
            return "redirect:/myTickets";
        } else {
            model.addAttribute("error", "Trip not found!");
            return "errorPage";
        }
    }




    @GetMapping("/downloadTicket/{ticketId}")
    public ResponseEntity<byte[]> downloadTicket(@PathVariable Long ticketId) throws IOException {
        Ticket ticket = ticketService.findTicketById(ticketId); // Assuming you have this method in your service
        if (ticket == null) {
            return ResponseEntity.notFound().build();
        }

        // Generate PDF
        ByteArrayOutputStream pdfOutputStream = pdfService.createStyledPdf(ticket);
        byte[] pdfBytes = pdfOutputStream.toByteArray();

        // Set headers to prompt a download
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=ticket_" + ticketId + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }






//    @GetMapping("/ticket/{ticketId}/pdf")
//    public ResponseEntity<byte[]> getTicketPdf(@PathVariable Long ticketId) {
//        Ticket ticket = ticketService.findTicketById(ticketId);
//        if (ticket != null) {
//            ByteArrayOutputStream pdfOutputStream = pdfService.createPdf(ticket);
//            byte[] pdfContent = pdfOutputStream.toByteArray();
//
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ticket_" + ticketId + ".pdf")
//                    .contentType(MediaType.APPLICATION_PDF)
//                    .body(pdfContent);
//        }
//        return ResponseEntity.notFound().build();
//    }






    @GetMapping("/myTickets")
    public String getUserTickets(Model model, Principal principal) {
        String username = principal.getName(); // Get the logged-in username
        User employee = userService.findUserByEmail(username); // Find user by email

        // Fetch tickets cut by this employee
        List<Ticket> userTickets = ticketService.getTicketsByEmployee(employee);

        model.addAttribute("userTickets", userTickets);
        return "myTickets"; // HTML page to display the tickets
    }

    @GetMapping("/usersTickets")
    public String getUsersTickets(Model model, Principal principal) {
        // Fetch tickets cut by this employee
        List<Ticket> userTickets = ticketService.getAllTickets();

        model.addAttribute("userTickets", userTickets);
        return "usersTickets"; // HTML page to display the tickets
    }



    @GetMapping("/ticketsForTrip/{ticketId}")
    public String getTicketsForTrip(@PathVariable Long ticketId ,Model model, Principal principal) {

        // Fetch tickets cut by this employee
        List<Ticket> userTickets = ticketService.getTicketsForTrip(ticketId);

        model.addAttribute("userTickets", userTickets);
        return "myTickets"; // HTML page to display the tickets
    }

    @PostMapping("/ticket/{id}/delete")
    public String deleteTIcket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return "redirect:/trips/allTrips"; // Redirect after deletion
    }





    @GetMapping("/allTickets")
    public String getAllTickets(Model model, Principal principal) {
        String username = principal.getName(); // Get the logged-in username
        User employee = userService.findUserByEmail(username); // Find user by email

        // Fetch tickets cut by this employee
        List<Ticket> userTickets = ticketService.getAllTickets();

        model.addAttribute("userTickets", userTickets);
        return "allTickets"; // HTML page to display the tickets
    }

    @GetMapping("/usersTickets/searchTickets")
    public String searchTicketsByDateRange(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            Model model) {

        // Fetch tickets within the date range
        List<Ticket> tickets = ticketService.findTicketsByDateRange(startDate, endDate);

        // Add tickets to the model
        if (tickets.isEmpty()) {
            model.addAttribute("message", "No tickets found for the selected date range.");
        } else {
            model.addAttribute("userTickets", tickets);
        }

        return "usersTickets"; // Update to your actual view name
    }



}
