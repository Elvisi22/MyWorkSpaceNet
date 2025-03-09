package com.travelagency.busbooking.service;

import com.travelagency.busbooking.entity.Job;
import com.travelagency.busbooking.entity.User;
import com.travelagency.busbooking.enums.JobStatus;
import com.travelagency.busbooking.repository.JobRepository;
import com.travelagency.busbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public Job postJob(Long userId , String title , String description , String location , String jobType ,
                       String salary){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found"));
        Job job = new Job();
        job.setUser(user);
        job.setTitle(title);
        job.setDescription(description);
        job.setLocation(location);
        job.setJobType(jobType);
        job.setSalary(salary);
        job.setJobStatus(JobStatus.OPEN);
        return jobRepository.save(job);
    }

    public List<Job> getAllJobs(){
        List<Job> job = jobRepository.findAll();
        job.sort(Comparator.comparing(Job::getId).reversed());
        return job;
    }

    public List<Job> getJobsPostedByUser(Long userId){
        List<Job> jobs = jobRepository.findByUserId(userId);
        jobs.sort(Comparator.comparing(Job::getId).reversed());
        return jobs;
    }

    public Job getJobById(Long id){
        return jobRepository.getById(id);
    }

    public List<Job> searchForJob(String title) {
        List<Job> jobs = jobRepository.findByTitleContainingIgnoreCase(title);

        return jobs.stream()
                .filter(job -> JobStatus.OPEN.equals(job.getJobStatus()))  // Assuming the 'Job' entity has a 'status' field
                .collect(Collectors.toList());
    }

}
