package com.LibraryManagementSystem.LMS.services;

import com.LibraryManagementSystem.LMS.dto.AddRoleDto;
import com.LibraryManagementSystem.LMS.dto.ReturnRoleDto;
import com.LibraryManagementSystem.LMS.domain.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    void addNewRole(AddRoleDto request);

    List<ReturnRoleDto> findAll();

    ReturnRoleDto findRoleById(Long roleId);
}
