package com.LibraryManagementSystem.LMS.dto;

import com.LibraryManagementSystem.LMS.domain.Role;
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

    private String username;

    private String email;

    private String roleName;
}
