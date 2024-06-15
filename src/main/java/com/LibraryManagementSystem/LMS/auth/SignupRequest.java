package com.LibraryManagementSystem.LMS.auth;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class SignupRequest {

    @NotBlank(message = "User name is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
