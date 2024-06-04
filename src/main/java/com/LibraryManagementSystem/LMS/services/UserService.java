package com.LibraryManagementSystem.LMS.services;

import com.LibraryManagementSystem.LMS.auth.LoginRequest;
import com.LibraryManagementSystem.LMS.auth.SignupRequest;
import com.LibraryManagementSystem.LMS.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    String authenticateUser(LoginRequest request);

    void registerUser(SignupRequest request);
}
