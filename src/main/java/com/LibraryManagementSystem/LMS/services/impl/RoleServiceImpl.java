package com.LibraryManagementSystem.LMS.services.impl;

import com.LibraryManagementSystem.LMS.dto.AddRoleDto;
import com.LibraryManagementSystem.LMS.dto.ReturnRoleDto;
import com.LibraryManagementSystem.LMS.domain.Role;
import com.LibraryManagementSystem.LMS.exceptions.RoleAlreadyExistsException;
import com.LibraryManagementSystem.LMS.mappers.impl.RoleRequestMapper;
import com.LibraryManagementSystem.LMS.mappers.impl.RoleReturnMapper;
import com.LibraryManagementSystem.LMS.repositories.RoleRepository;
import com.LibraryManagementSystem.LMS.services.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleRequestMapper roleRequestMapper;
    private final RoleReturnMapper roleReturnMapper;

    public RoleServiceImpl(RoleRepository roleRepository, RoleRequestMapper roleRequestMapper, RoleReturnMapper roleReturnMapper) {
        this.roleRepository = roleRepository;
        this.roleRequestMapper = roleRequestMapper;
        this.roleReturnMapper = roleReturnMapper;
    }

    @Override
    public void addNewRole(AddRoleDto request) {

        if (roleRepository.existsByName(request.getName())) {
            throw new RoleAlreadyExistsException("Role with name: " + request.getName() + " already exists");
        }

        Role role = roleRequestMapper.mapFrom(request);

        roleRepository.save(role);
    }

    @Override
    public List<ReturnRoleDto> findAll() {
        return roleRepository.findAll().stream()
                .map(roleReturnMapper::mapTo)
                .collect(Collectors.toList());
    }
}
