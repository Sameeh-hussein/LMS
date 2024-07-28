package com.LibraryManagementSystem.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReturnUserDto {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String roleName;

    private String profileImage;
}
