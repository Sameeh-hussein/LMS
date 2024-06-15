package com.LibraryManagementSystem.LMS.mappers.impl;

import com.LibraryManagementSystem.LMS.dto.AddRoleDto;
import com.LibraryManagementSystem.LMS.domain.Role;
import com.LibraryManagementSystem.LMS.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RoleRequestMapper implements Mapper<Role, AddRoleDto> {

    private final ModelMapper modelMapper;

    public RoleRequestMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public AddRoleDto mapTo(Role role) {
        return modelMapper.map(role, AddRoleDto.class);
    }

    @Override
    public Role mapFrom(AddRoleDto addRoleDto) {
        return modelMapper.map(addRoleDto, Role.class);
    }
}
