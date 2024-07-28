package com.LibraryManagementSystem.LMS.controller;

import com.LibraryManagementSystem.LMS.dto.AddRoleDto;
import com.LibraryManagementSystem.LMS.dto.ReturnRoleDto;
import com.LibraryManagementSystem.LMS.services.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Roles")
@RequestMapping(value = "/api/roles")
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "Get all roles")
    public ResponseEntity<List<ReturnRoleDto>> getRoles() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(roleService.findAll());
    }

    @GetMapping(value = "{roleId}")
    @Operation(summary = "Get a specific role by its ID")
    public ResponseEntity<ReturnRoleDto> getRole(@PathVariable Long roleId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(roleService.findRoleById(roleId));
    }

    @PostMapping
    @Operation(summary = "Add a new role entry")
    public ResponseEntity<String> addRole(@Valid @RequestBody AddRoleDto request) {
        roleService.addNewRole(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Role added successfully");
    }
}
