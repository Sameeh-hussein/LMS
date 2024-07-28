package com.LibraryManagementSystem.LMS.controller;

import com.LibraryManagementSystem.LMS.auth.*;
import com.LibraryManagementSystem.LMS.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication")
@RequestMapping(value = "/api/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Login a user")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {

        String token = userService.authenticateUser(request);

        return ResponseEntity.status((HttpStatus.OK))
                .body(token);
    }

    @PostMapping("/register-member")
    @Operation(summary = "Register a member user")
    public ResponseEntity<String> registerMember(@Valid @RequestBody SignupRequest request) {

        userService.registerUserWithRole(request, "ROLE_MEMBER");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User added successfully");
    }

    @PostMapping("/register-librarian")
    @Operation(summary = "Register a librarian user")
    public ResponseEntity<String> registerLibrarian(@Valid @RequestBody SignupRequest request) {

        userService.registerUserWithRole(request, "ROLE_LIBRARIAN");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User added successfully");
    }

    @PostMapping("/register-admin")
    @Operation(summary = "Register an admin user")
    public ResponseEntity<String> registerAdmin(@Valid @RequestBody SignupRequest request) {

        userService.registerUserWithRole(request, "ROLE_ADMIN");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User added successfully");
    }
}
