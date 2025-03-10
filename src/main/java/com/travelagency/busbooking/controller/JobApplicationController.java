package com.travelagency.busbooking.controller;

import com.travelagency.busbooking.entity.Job;
import com.travelagency.busbooking.entity.JobApplication;
import com.travelagency.busbooking.entity.User;
import com.travelagency.busbooking.enums.ApplicationStatus;
import com.travelagency.busbooking.repository.JobApplicationRepository;
import com.travelagency.busbooking.service.JobApplicationService;
import com.travelagency.busbooking.service.JobService;
import com.travelagency.busbooking.service.UserService;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/applications")
public class JobApplicationController {
    private final JobApplicationService jobApplicationService;
    private final JobService jobService;
    private final UserService userService;
    private final JobApplicationRepository applicationRepository;

    @Autowired
    public JobApplicationController(JobApplicationService jobApplicationService, JobService jobService, UserService userService, JobApplicationRepository applicationRepository) {
        this.jobApplicationService = jobApplicationService;
        this.jobService = jobService;
        this.userService = userService;
        this.applicationRepository = applicationRepository;
    }

    @GetMapping("/apply/{jobId}")
    public String showApplicationForm(@PathVariable Long jobId, Model model) {
        Job job = jobService.getJobById(jobId);
        model.addAttribute("job", job);
        model.addAttribute("jobApplication", new JobApplication());
        return "applyJobForm";
    }

    @PostMapping("/apply")
    public String applyForJob(@RequestParam("jobId") Long jobId,
                              @RequestParam("resume") MultipartFile resumeFile,
                              @RequestParam("coverLetter") String coverLetter,
                              Principal principal) throws IOException {
        User applicant = userService.findUserByEmail(principal.getName());
        Job job = jobService.getJobById(jobId);

        jobApplicationService.applyForJob(applicant, job, resumeFile, coverLetter);

        return "redirect:/applications/my";
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<JobApplication>> getApplicationsByUser(@PathVariable Long userId) {
        List<JobApplication> applications = jobApplicationService.getApplicationsByUser(userId);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/job/{jobId}")
    public String getApplicationsByJob(@PathVariable Long jobId,
    Model model , Principal principal) {

        List<JobApplication> applicationsDone = jobApplicationService.getApplicationsByJob(jobId);
        model.addAttribute("createdapplications", applicationsDone);
        return "created-applications";
    }

    @GetMapping("/my")
    public String viewMyApplications(Model model, Principal principal) {
        User user = userService.findUserByEmail(principal.getName()); // Get logged-in user
        List<JobApplication> applications = jobApplicationService.getApplicationsByUser(user.getId());
        model.addAttribute("jobapplications", applications);
        return "user-applications";
    }

    @GetMapping("/review/{applicationId}")
    public String review(@PathVariable("applicationId") Long applicationId ,
                         ApplicationStatus status,
                         Model model){
        JobApplication jobApplication = jobApplicationService.confirmOrDeleteApplication(applicationId ,status);
        model.addAttribute("reviewApplication" , jobApplication);
        return "reviewApplication";


    }
    @PostMapping("/review/{applicationId}/update")
    public String updateApplicationStatus(@PathVariable("applicationId") Long applicationId,
                                          @RequestParam("status") ApplicationStatus status) {
        jobApplicationService.confirmOrDeleteApplication(applicationId, status);
        return "redirect:/job/alljobs";
    }

    @GetMapping("/delete/{applicationId}")
    public String deleteApplication(@PathVariable("applicationId") Long applicationId){
        jobApplicationService.deleteMyApplication(applicationId);
        return "user-applications";

    }

    @GetMapping("/resume/{id}")
    public ResponseEntity<Resource> downloadResume(@PathVariable Long id) {

        JobApplication application = jobApplicationService.findById(id);


        ByteArrayResource resource = new ByteArrayResource(application.getResume());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=resume.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
