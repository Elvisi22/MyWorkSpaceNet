//package com.travelagency.busbooking.config;
//
//import com.travelagency.busbooking.entity.Role;
//import com.travelagency.busbooking.entity.User;
//import com.travelagency.busbooking.repository.RoleRepository;
//import com.travelagency.busbooking.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//
//
//
//    @Component
//    public class AdminUserInitializer implements CommandLineRunner {
//
//        @Autowired
//        private UserRepository userRepository;
//
//        @Autowired
//        private RoleRepository roleRepository;
//
//        @Override
//        public void run(String... args) {
//            // Create a new role
//            Role role = new Role();
//            role.setName("ROLE_CAKARIDO");
//            roleRepository.save(role); // Save the Role first
//
//            // Create the admin user
//            User adminUser = new User();
//            adminUser.setName("Admin");
//            adminUser.setEmail("admin@example.com");
//            adminUser.setPassword("password"); // Use a hashed password in production
//            adminUser.getRoles().add(role); // Associate the role with the user
//
//            userRepository.save(adminUser); // Now save the user
//        }
//    }