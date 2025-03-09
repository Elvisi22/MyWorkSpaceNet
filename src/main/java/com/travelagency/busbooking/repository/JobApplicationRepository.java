package com.travelagency.busbooking.repository;

import com.travelagency.busbooking.entity.Job;
import com.travelagency.busbooking.entity.JobApplication;
import com.travelagency.busbooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByUserId(Long id);

    List<JobApplication> findByJobId(Long jobId);

    // Find applications by user
    List<JobApplication> findByUser(User user);

    // Find applications by job
    List<JobApplication> findByJob(Job job);
}
