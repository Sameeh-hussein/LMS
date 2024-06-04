package com.LibraryManagementSystem.LMS.auth;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.NonNull;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class SignupRequest {

    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
}
