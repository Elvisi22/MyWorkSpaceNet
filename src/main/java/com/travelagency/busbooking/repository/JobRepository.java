package com.travelagency.busbooking.repository;

import com.travelagency.busbooking.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job , Long> {
    List<Job> findByUserId(Long userId);
    List<Job> findByTitleContainingIgnoreCase(String title);
}
