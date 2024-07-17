package com.LibraryManagementSystem.LMS.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDataRequest {
    @NotBlank(message = "Password is required")
    private String password;

    @Valid
    private SignupRequest data;
}
