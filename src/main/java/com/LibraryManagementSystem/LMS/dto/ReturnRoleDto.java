package com.LibraryManagementSystem.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnRoleDto {
    private Long id;
    private String name;
}
