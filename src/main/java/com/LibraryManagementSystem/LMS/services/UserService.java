package com.LibraryManagementSystem.LMS.services;

import com.LibraryManagementSystem.LMS.auth.LoginRequest;
import com.LibraryManagementSystem.LMS.auth.SignupRequest;
import com.LibraryManagementSystem.LMS.auth.UpdateDataRequest;
import com.LibraryManagementSystem.LMS.domain.User;
import com.LibraryManagementSystem.LMS.dto.ReturnUserDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface UserService {

    String authenticateUser(LoginRequest request);

    void registerUserWithRole(SignupRequest request, String Role);

    List<ReturnUserDto> findAll();

    ReturnUserDto findUserById(Long id);

    void updateUserData(Long userId, UpdateDataRequest request);
}
