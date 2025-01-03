package com.travelagency.busbooking.repository;

import com.travelagency.busbooking.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    Trip findByDepartureDateAndBusId(LocalDate date, Long busId);  // To check if a trip already exists for the bus


    List<Trip> findByDepartureDate(LocalDate departureDate);

    // Modify this method to use LocalDate for date comparisons and String for time
    // If you want to find trips based on a departure time, use a String comparison
    List<Trip> findByDepartureTime(String departureTime);

    List<Trip> findByDepartureDateBetween(LocalDate startDate, LocalDate endDate);

    List<Trip> findByOriginAndDestination(String origin, String destination);

}
