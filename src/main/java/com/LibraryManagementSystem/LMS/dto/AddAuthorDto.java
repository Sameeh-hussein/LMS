package com.LibraryManagementSystem.LMS.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddAuthorDto {
    @NotBlank(message = "Author name is required")
    private String name;
}
