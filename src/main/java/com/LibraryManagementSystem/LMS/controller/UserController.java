package com.LibraryManagementSystem.LMS.controller;

import com.LibraryManagementSystem.LMS.auth.UpdateDataRequest;
import com.LibraryManagementSystem.LMS.dto.ReturnUserDto;
import com.LibraryManagementSystem.LMS.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Users")
@RequestMapping(value = "/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users")
    public ResponseEntity<List<ReturnUserDto>> getUsers() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.findAll());
    }

    @GetMapping(value = "{userId}")
    @Operation(summary = "Get a specific user by its ID")
    public ResponseEntity<ReturnUserDto> getUser(@PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.findUserById(userId));
    }

    @PutMapping(value = "{userId}/data")
    @Operation(summary = "Update user data, for logged in user")
    public ResponseEntity<String> updateUserData(
            @PathVariable Long userId, @RequestBody UpdateDataRequest request
    ) {
        userService.updateUserData(userId, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body("User data updated successfully");
    }

    @PostMapping(value = "profile-image")
    @Operation(summary = "Set user profile image, for logged in user")
    public ResponseEntity<String> updateUserProfileImage(@RequestParam("image") MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(userService.updateUserProfileImage(file));
    }
}
