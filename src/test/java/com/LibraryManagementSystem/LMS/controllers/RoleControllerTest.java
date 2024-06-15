package com.LibraryManagementSystem.LMS.controllers;

import com.LibraryManagementSystem.LMS.TestDataUtil;
import com.LibraryManagementSystem.LMS.domain.Role;
import com.LibraryManagementSystem.LMS.dto.AddRoleDto;
import com.LibraryManagementSystem.LMS.dto.ReturnRoleDto;
import com.LibraryManagementSystem.LMS.repositories.RoleRepository;
import com.LibraryManagementSystem.LMS.services.RoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.util.Arrays;
import java.util.List;

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

    @Autowired
    private RoleRepository roleRepository;

    @MockBean
    private RoleService roleService;

    @Test
    public void testThatAddRoleReturnHttp201CreatedWhenAddedSuccessfully() throws Exception {
        AddRoleDto addRoleDto = AddRoleDto.builder().name("ROLE_ADMIN").build();

        String roleJson = objectMapper.writeValueAsString(addRoleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roleJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    public void testThatAddRoleReturnHttp209CreatedWhenRoleExist() throws Exception {
        AddRoleDto addRoleDto = AddRoleDto.builder().name("ROLE_MEMBER").build();

        Role role = testDataUtil.createRoleForTest();

        roleRepository.save(role);

        String roleJson = objectMapper.writeValueAsString(addRoleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roleJson)
        ).andExpect(
                MockMvcResultMatchers.status().isConflict()
        );
    }

    @Test
    public void testThatGetRolesReturned200OkWhenGetRolesSuccess() throws Exception {
        ReturnRoleDto addRoleDto1 = ReturnRoleDto.builder().name("ROLE_ADMIN").build();
        ReturnRoleDto addRoleDto2 = ReturnRoleDto.builder().name("ROLE_MEMBER").build();

        List<ReturnRoleDto> roles = Arrays.asList(addRoleDto1, addRoleDto2);

        when(roleService.findAll()).thenReturn(roles);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.length()").value(roles.size())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value("ROLE_ADMIN")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].name").value("ROLE_MEMBER")
        );
    }
}
