package com.travelagency.busbooking.controller;

import com.travelagency.busbooking.entity.Job;
import com.travelagency.busbooking.entity.User;
import com.travelagency.busbooking.service.JobService;
import com.travelagency.busbooking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/job")
public class JobController {

    private JobService jobService;
    private UserService userService;

    public JobController(JobService jobService, UserService userService) {
        this.jobService = jobService;
        this.userService = userService;
    }

    @GetMapping("/create")
    public String createJob(Model model){
        Job job = new Job();
        model.addAttribute("job" , job);
        return "JobForm";
    }

    @PostMapping("/create")
    public String createJobs(@RequestParam("title") String title,
                             @RequestParam("description") String description,
                             @RequestParam("location") String location,
                             @RequestParam("jobType") String jobType,
                             @RequestParam("salary") String salary,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {

        String email = principal.getName();
        User currentUser = userService.findUserByEmail(email);

        Job job = new Job();
        job.setUser(currentUser);
        job.setTitle(title);
        job.setDescription(description);
        job.setLocation(location);
        job.setJobType(jobType);
        job.setSalary(salary);

        jobService.postJob(currentUser.getId(), job.getTitle(), job.getDescription(),
                job.getLocation(), job.getJobType(), job.getSalary());

        redirectAttributes.addFlashAttribute("successMessage", "Job posted successfully!");

        return "redirect:/job/create";
    }


    @GetMapping("/alljobs")
    public String getAllJosts(Model model, Principal principal) {
        List<Job> jobs = jobService.getAllJobs();
        model.addAttribute("jobs" , jobs);
        return "allJobs";
    }

    @GetMapping("/alljobsposted")
    public String getAllJostsAsJobSeeker(Model model, Principal principal) {
        List<Job> jobs = jobService.getAllJobs();
        model.addAttribute("jobs" , jobs);
        return "AllJobsAsJobSeeker";
    }

    @GetMapping("/myjobs")
    public String getMyJosts(Model model, Principal principal) {
        String email = principal.getName();
        System.out.println(email);
        User user = userService.findUserByEmail(principal.getName());
        List<Job> jobs = jobService.getJobsPostedByUser(user.getId());
        model.addAttribute("jobs" , jobs);
        return "allJobs";
    }


    @GetMapping("/search")
    public String searchJobsByTitle(@RequestParam(value = "title", required = false) String title, Model model) {
        // Call the service method to search jobs by title
        List<Job> jobs = title != null ? jobService.searchForJob(title) : new ArrayList<>();
        model.addAttribute("jobs", jobs);
        model.addAttribute("title", title);  // So we can retain the search query in the form
        return "alljobs";  // Thymeleaf template for displaying jobs
    }









}
