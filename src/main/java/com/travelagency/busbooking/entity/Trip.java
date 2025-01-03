package com.travelagency.busbooking.entity;
import jakarta.persistence.*;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trip")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean active;

    private LocalDate departureDate;

    private String destination;

    private String origin;

    private int availableCapacity;

    @ManyToOne
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    // New field to hold tickets
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<Ticket> bookedTickets = new ArrayList<>();

    public List<Ticket> getBookedTickets() {
        return bookedTickets;
    }

    private String departureTime; // e.g., "9AM" or "9PM"

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public List<Ticket> getTickets() {
        return bookedTickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.bookedTickets = tickets;
    }

    public Trip() {
        this.availableCapacity = 55; // Initialize capacity to 50
        this.bookedTickets = new ArrayList<>();
    }
    public void addTicket(Ticket ticket) {
        if (availableCapacity > 0) {
            bookedTickets.add(ticket);
            ticket.setTrip(this);
            reduceCapacity(1); // Reduce capacity by 1 for each booking
        } else {
            throw new RuntimeException("No available seats.");
        }

    }

    public int getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(int availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public void reduceCapacity(int amount) {
        if (this.availableCapacity - amount >= 0) {
            this.availableCapacity -= amount;
        } else {
            throw new RuntimeException("Not enough available seats.");
        }
    }


}