package com.LibraryManagementSystem.LMS.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddAuthorDto {
    @NotBlank(message = "Author name is required")
    private String name;
}
