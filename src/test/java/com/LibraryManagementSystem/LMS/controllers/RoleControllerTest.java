package com.LibraryManagementSystem.LMS.controllers;

import com.LibraryManagementSystem.LMS.TestDataUtil;
import com.LibraryManagementSystem.LMS.domain.Role;
import com.LibraryManagementSystem.LMS.dto.AddRoleDto;
import com.LibraryManagementSystem.LMS.dto.ReturnRoleDto;
import com.LibraryManagementSystem.LMS.exceptions.RoleAlreadyExistsException;
import com.LibraryManagementSystem.LMS.exceptions.RoleNotFoundException;
import com.LibraryManagementSystem.LMS.repositories.RoleRepository;
import com.LibraryManagementSystem.LMS.services.RoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestDataUtil testDataUtil;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private RoleService roleService;

    @Test
    public void testThatAddRoleReturnHttp201CreatedWhenAddedSuccessfully() throws Exception {
        Role role = Role.builder().id(1L).name("ROLE_LIBRARIAN").build();

        when(roleRepository.save(role)).thenReturn(role);

        String roleJson = objectMapper.writeValueAsString(role);

        var result = mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(roleJson)
        );

        result.andExpect(status().isCreated());
    }

    @Test
    public void testThatAddRoleReturnHttp409ConflictWhenRoleExist() throws Exception {
        AddRoleDto role = AddRoleDto.builder().name("ROLE_LIBRARIAN").build();

        doThrow(new RoleAlreadyExistsException("Role with name: ROLE_LIBRARIAN already exists"))
                .when(roleService).addNewRole(any(AddRoleDto.class));

        String roleJson = objectMapper.writeValueAsString(role);

        var result = mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(roleJson)
        );

        result.andExpect(status().isConflict());
    }

    @Test
    public void testThatGetRolesReturns200OkWhenGetRolesSuccess() throws Exception {
        ReturnRoleDto addRoleDto1 = ReturnRoleDto.builder().name("ROLE_ADMIN").build();
        ReturnRoleDto addRoleDto2 = ReturnRoleDto.builder().name("ROLE_MEMBER").build();

        when(roleService.findAll()).thenReturn(List.of(addRoleDto1, addRoleDto2));

        var result = mockMvc.perform(get("/api/roles"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$[1].name").value("ROLE_MEMBER"));
    }

    @Test
    public void testThatGetRoleByIdReturns200OkWhenRoleExist() throws Exception {
        ReturnRoleDto role = ReturnRoleDto.builder().id(1L).name("ROLE_ADMIN").build();

        when(roleService.findRoleById(1L)).thenReturn(role);

        var result = mockMvc.perform(get("/api/roles/1"));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testThatGetRoleByIdReturns404NotFoundWhenRoleDoesNotExist() throws Exception {
        Long roleId = 999L;
        String errorMessage = "Role with id: " + roleId + " does not exist";

        when(roleService.findRoleById(roleId)).thenThrow(new RoleNotFoundException(errorMessage));

        var result = mockMvc.perform(get("/api/roles/{roleId}", roleId)) ;

        result.andExpect(status().isNotFound());
    }
}
