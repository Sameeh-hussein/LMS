package com.LibraryManagementSystem.LMS.mappers.impl;

import com.LibraryManagementSystem.LMS.dto.ReturnRoleDto;
import com.LibraryManagementSystem.LMS.domain.Role;
import com.LibraryManagementSystem.LMS.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleReturnMapper implements Mapper<Role, ReturnRoleDto> {
    private final ModelMapper modelMapper;

    @Override
    public ReturnRoleDto mapTo(Role role) {
        return modelMapper.map(role, ReturnRoleDto.class);
    }

    @Override
    public Role mapFrom(ReturnRoleDto returnRoleDto) {
        return modelMapper.map(returnRoleDto, Role.class);
    }
}
