package com.travelagency.busbooking.controller;

import com.travelagency.busbooking.entity.Bus;
import com.travelagency.busbooking.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/buses")
public class BusController {

    @Autowired
    private BusService busService;

    // Show the form to create a new bus
    @GetMapping("/create")
    public String showCreateBusForm(Model model) {
        model.addAttribute("bus", new Bus());
        return "buss";
    }

    // Handle the form submission
    @PostMapping("/create")
    public String createBus(@ModelAttribute("bus") Bus bus) {
        busService.createBuss(bus.getName());
        return "redirect:/trips"; // Redirect to a list of buses or a confirmation page
    }
}