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
                                .requestMatchers("/register/**").permitAll()
                                .requestMatchers("/login").permitAll()

                                .requestMatchers("/job/create").hasRole("USER")
                                .requestMatchers("/job/**").authenticated()
                                .requestMatchers("/applications/job/**").authenticated()
                                .requestMatchers("/applications/job/create").hasRole("USER")
                                .requestMatchers("/applications/apply/**").authenticated()
                                .requestMatchers("/applications/review/**").authenticated()
                                .requestMatchers("/applications/resume/**").authenticated()
                                .requestMatchers("/applications/my").authenticated()
                                .requestMatchers("/applications/delete/**").authenticated()
                                .requestMatchers("/profile").authenticated()
                                .requestMatchers("/update/**").authenticated()
                                .requestMatchers("/index").authenticated()

                                .requestMatchers("/successPage").authenticated()
                                .requestMatchers("/users/**").hasRole("ADMIN")
                                .requestMatchers("/roles/**").hasRole("ADMIN")
                                .requestMatchers("/user/delete/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                ).formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/index")
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
