package com.LibraryManagementSystem.LMS.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

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
