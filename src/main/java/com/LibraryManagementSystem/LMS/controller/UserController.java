package com.LibraryManagementSystem.LMS.controller;

import com.LibraryManagementSystem.LMS.auth.SignupRequest;
import com.LibraryManagementSystem.LMS.auth.UpdateDataRequest;
import com.LibraryManagementSystem.LMS.domain.User;
import com.LibraryManagementSystem.LMS.dto.ReturnUserDto;
import com.LibraryManagementSystem.LMS.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<ReturnUserDto>> getUsers() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.findAll());
    }

    @GetMapping(value = "{userId}")
    public ResponseEntity<ReturnUserDto> getUser(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.findUserById(userId));
    }

    @PutMapping(value = "{userId}/data")
    public ResponseEntity<String> updateUserData(
            @PathVariable Long userId, @RequestBody UpdateDataRequest request
            ) {
        userService.updateUserData(userId, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body("User data updated successfully");
    }
}
