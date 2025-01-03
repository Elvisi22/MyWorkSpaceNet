package com.travelagency.busbooking.repository;

import com.travelagency.busbooking.entity.Ticket;
import com.travelagency.busbooking.entity.Trip;
import com.travelagency.busbooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByEmployee(User employee);

    Ticket findTicketById(Long id);

    List<Ticket> findTicketByTripId(Long id);

    List<Ticket> findByBookingTimeBetween(LocalDate startDate, LocalDate endDate);

}
