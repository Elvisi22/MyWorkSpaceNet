package com.travelagency.busbooking.repository;

import com.travelagency.busbooking.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {

    @Query("SELECT b FROM Bus b WHERE b.name = :nameOrId OR b.id = :nameOrId")
    Optional<Bus> findByNameOrId(@Param("nameOrId") String nameOrId);

}
