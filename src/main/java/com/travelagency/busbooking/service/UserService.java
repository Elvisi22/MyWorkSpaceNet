package com.travelagency.busbooking.service;

import com.travelagency.busbooking.dto.UserDto;
import com.travelagency.busbooking.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();

    void deleteUserById(Long id);
}
