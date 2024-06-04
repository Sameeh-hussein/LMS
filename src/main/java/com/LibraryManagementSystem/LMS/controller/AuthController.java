package com.LibraryManagementSystem.LMS.controller;

import com.LibraryManagementSystem.LMS.auth.*;
import com.LibraryManagementSystem.LMS.mappers.impl.UserMapper;
import com.LibraryManagementSystem.LMS.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {

        String token = userService.authenticateUser(request);

        return ResponseEntity.status((HttpStatus.OK))
                .body(token);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest request) {

        userService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User added successfully");
    }
}
