package com.travelagency.busbooking.repository;


import com.travelagency.busbooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    User findByName(String name);

    boolean existsByEmail(String email);





}
