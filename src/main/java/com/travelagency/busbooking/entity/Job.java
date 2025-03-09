package com.travelagency.busbooking.entity;

import com.travelagency.busbooking.enums.JobStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String location;
    private String jobType;
    private String salary;
    private JobStatus jobStatus;

    @ManyToOne
    @JoinColumn(name = "user_id" , nullable = false)
    private User user;


}
