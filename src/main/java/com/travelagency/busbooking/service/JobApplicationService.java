package com.travelagency.busbooking.service;

import com.travelagency.busbooking.entity.Job;
import com.travelagency.busbooking.entity.JobApplication;
import com.travelagency.busbooking.entity.User;
import com.travelagency.busbooking.enums.ApplicationStatus;
import com.travelagency.busbooking.repository.JobApplicationRepository;
import com.travelagency.busbooking.repository.JobRepository;
import com.travelagency.busbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobApplicationService {
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    @Autowired
    public JobApplicationService(JobApplicationRepository jobApplicationRepository, UserRepository userRepository, JobRepository jobRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public JobApplication applyForJob(User applicant, Job job, String resume, String coverLetter) {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setUser(applicant);
        jobApplication.setJob(job);
        jobApplication.setResume(resume);
        jobApplication.setCoverLetter(coverLetter);
        jobApplication.setApplicationStatus(ApplicationStatus.PENDING);
        return jobApplicationRepository.save(jobApplication);
    }

    public List<JobApplication> getApplicationsForJob(Long jobId) {
        return jobApplicationRepository.findAll(); // Ideally, filter by jobId
    }

    public List<JobApplication> getApplicationsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return jobApplicationRepository.findByUser(user);
    }


    public List<JobApplication> getApplicationsByJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return jobApplicationRepository.findByJob(job);
    }

    public JobApplication confirmOrDeleteApplication(Long applicationId , ApplicationStatus response){
        JobApplication jobApplication = jobApplicationRepository.getById(applicationId);
        jobApplication.setApplicationStatus(response);
        jobApplicationRepository.save(jobApplication);
        return jobApplication;
    }

    public String deleteMyApplication(Long id){
        JobApplication jobApplication = jobApplicationRepository.getById(id);
        jobApplicationRepository.delete(jobApplication);
        return "deleted";
    }


}