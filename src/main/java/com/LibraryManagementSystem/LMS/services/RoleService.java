package com.LibraryManagementSystem.LMS.services;

import com.LibraryManagementSystem.LMS.dto.AddRoleDto;
import com.LibraryManagementSystem.LMS.dto.ReturnRoleDto;

import java.util.List;

public interface RoleService {
    void addNewRole(AddRoleDto request);

    List<ReturnRoleDto> findAll();

    ReturnRoleDto findRoleById(Long roleId);
}
