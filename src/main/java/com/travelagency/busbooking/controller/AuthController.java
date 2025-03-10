package com.travelagency.busbooking.controller;

import com.travelagency.busbooking.dto.UserDto;
import com.travelagency.busbooking.entity.User;
import com.travelagency.busbooking.service.UserService;
import com.travelagency.busbooking.service.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
public class AuthController {

    private UserService userService;
    private UserServiceImpl userServiceimp;

    public AuthController(UserService userService , UserServiceImpl userServiceimp) {
        this.userService = userService;
        this.userServiceimp = userServiceimp;
    }

    @GetMapping("login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/index")
    public String indexpage(){
        return "index";
    }

    @GetMapping("register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto user,
                               BindingResult result,
                               Model model){
        User existing = userService.findUserByEmail(user.getEmail());
        if (existing != null) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }
        userService.saveUser(user);
        return "redirect:/login";
    }

    @GetMapping("/users")
    public String listRegisteredUsers(Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/profile")
    public String showUpdateForm(Model model , Principal principal) {
        // Fetch the user and convert to DTO for pre-filling the form
        String email = principal.getName();
        User user = userService.findUserByEmail(email);
        UserDto userDto = new UserDto();
        String[] str = user.getName().split(" ");
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());

        model.addAttribute("userDto", userDto);
        model.addAttribute("userId", user.getId());
        return "updateForm";
    }


    @PostMapping("/update/{userId}")
    public String updateUser(@PathVariable Long userId, @ModelAttribute UserDto userDto) {
        // Call the service to update the user details
        userServiceimp.updateUser(userId, userDto);
        return "redirect:/index";  // Redirect to a page displaying the user list or profile
    }


    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userServiceimp.deleteUserById(id);
        return "redirect:/users";
    }




}