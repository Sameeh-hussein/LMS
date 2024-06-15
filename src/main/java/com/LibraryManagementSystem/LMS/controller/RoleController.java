package com.LibraryManagementSystem.LMS.controller;

import com.LibraryManagementSystem.LMS.dto.AddRoleDto;
import com.LibraryManagementSystem.LMS.dto.ReturnRoleDto;
import com.LibraryManagementSystem.LMS.services.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<String> addRole(@Valid @RequestBody AddRoleDto request) {
        roleService.addNewRole(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Role added successfully");
    }

    @GetMapping
    public ResponseEntity<List<ReturnRoleDto>> getRoles() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(roleService.findAll());
    }
}