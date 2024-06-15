package com.LibraryManagementSystem.LMS.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReturnRoleDto {
    private Long id;
    private String name;
}
