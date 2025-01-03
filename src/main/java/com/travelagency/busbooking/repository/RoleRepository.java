package com.travelagency.busbooking.repository;

import com.travelagency.busbooking.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);


    Optional<Role> findRoleByName(String aLong);
}
