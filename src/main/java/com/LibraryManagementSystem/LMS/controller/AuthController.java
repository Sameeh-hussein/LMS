package com.LibraryManagementSystem.LMS.controller;

import com.LibraryManagementSystem.LMS.auth.*;
import com.LibraryManagementSystem.LMS.domain.User;
import com.LibraryManagementSystem.LMS.mappers.impl.UserMapper;
import com.LibraryManagementSystem.LMS.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {

        try {
            Optional<User> user = userService.findByEmail(request.getEmail());

            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found with email: " + request.getEmail());
            }

            if (passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Incorrect password");
            }

            return ResponseEntity.status((HttpStatus.OK))
                    .body(jwtUtil.generateToken(user.get().getUsername()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the login request");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest request) {
        try {

            boolean userExists = userService.existsByEmail(request.getEmail());
            if (userExists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("User with email: " + request.getEmail() + " already exists");
            }

            userExists = userService.existsByUsername(request.getUsername());
            if (userExists) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("User with user name: " + request.getUsername() + " already exists");
            }

            User userToAdd = userMapper.mapFrom(request);
            userToAdd.setPassword(passwordEncoder.encode(request.getPassword()));
            userToAdd.setCreatedAt(LocalDateTime.now());

            userService.save(userToAdd);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("User added successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request");
        }
    }
}
