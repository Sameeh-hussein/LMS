package com.LibraryManagementSystem.LMS.auth;

import lombok.Builder;
import lombok.Data;


import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UpdateDataRequest {
    @NotBlank(message = "Password is required")
    private String password;

    @Valid
    private SignupRequest data;
}
