package com.travelagency.busbooking.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())

                .authorizeHttpRequests((authorize) ->
                        authorize // Allow public access to the registration and login pages
                                .requestMatchers("/", "/static/**", "/css/**", "/js/**").permitAll()
                                .requestMatchers("/index").permitAll()
                                .requestMatchers("/register/**").hasRole("ADMIN")
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/rrethNesh").permitAll()
                                .requestMatchers("kontakt").permitAll()
                                .requestMatchers("/trips").authenticated()
                                .requestMatchers("/myTickets").authenticated()
                                .requestMatchers("/tripForm").authenticated()
                                .requestMatchers("/downloadTicket/**").authenticated()
                                .requestMatchers("/allTickets").authenticated()
                                .requestMatchers("/trips/allTrips").hasRole("ADMIN")
                                .requestMatchers("/ticketsForTrip/**").hasRole("ADMIN")
                                .requestMatchers("/buses/create").hasRole("ADMIN")
                                .requestMatchers("/buses/create").hasRole("ADMIN")
                                .requestMatchers("/usersTickets").hasRole("ADMIN")
                                .requestMatchers("/usersTickets/searchTickets").hasRole("ADMIN")
                                .requestMatchers("/trips/admin/uploadTrips").hasRole("ADMIN")


                                .requestMatchers("/destinacione").permitAll()
                                .requestMatchers("/rregullorja").permitAll()
                                // Allow logged-in users to access booking pages and trips
                                .requestMatchers("/trips/**").permitAll()
                                .requestMatchers("/trips/searchTrips").permitAll()
                                .requestMatchers("/bookTicket/**").authenticated() // Ensure you include any other booking-related paths
                                .requestMatchers("/successPage").authenticated()
                                .requestMatchers("/users").hasRole("ADMIN") // Admin access
                ).formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/trips")
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                );
        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
}
