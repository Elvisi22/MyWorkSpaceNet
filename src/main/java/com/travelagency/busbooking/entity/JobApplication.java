package com.travelagency.busbooking.entity;

import com.travelagency.busbooking.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    private String resume;
    private String coverLetter;
    private ApplicationStatus applicationStatus;
    private LocalDateTime appliedAt = LocalDateTime.now();
}
