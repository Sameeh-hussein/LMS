package com.LibraryManagementSystem.LMS.controllers;

import com.LibraryManagementSystem.LMS.TestDataUtil;
import com.LibraryManagementSystem.LMS.auth.LoginRequest;
import com.LibraryManagementSystem.LMS.auth.SignupRequest;
import com.LibraryManagementSystem.LMS.domain.Role;
import com.LibraryManagementSystem.LMS.domain.User;
import com.LibraryManagementSystem.LMS.repositories.RoleRepository;
import com.LibraryManagementSystem.LMS.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestDataUtil testDataUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testThatLoginReturnsHttp200OkWhenUserExist() throws Exception {
        User user = testDataUtil.createUserForTest();
        Role role = testDataUtil.createRoleForTest();
        roleRepository.save(role);
        user.setRole(role);

        userRepository.save(user);

        LoginRequest loginRequest = testDataUtil.createLoginRequestForTest();

        String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatLoginReturnsHttp404NotFoundWhenUserNotExist() throws Exception {
        LoginRequest loginRequest = testDataUtil.createLoginRequestForTest();

        String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testThatRegisterMemberReturnsHttp201CreatedWhenValidRegister() throws Exception {
        SignupRequest signupRequest = testDataUtil.createSignupRequestForTest();
        Role memberRole = Role.builder()
                .name("ROLE_MEMBER")
                .build();

        roleRepository.save(memberRole);

        String signupRequestJson = objectMapper.writeValueAsString(signupRequest);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/register-member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequestJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    public void testThatRegisterLibrarianReturnsHttp201CreatedWhenValidRegister() throws Exception {
        SignupRequest signupRequest = testDataUtil.createSignupRequestForTest();
        Role librarianRole = Role.builder()
                .name("ROLE_LIBRARIAN")
                .build();
        roleRepository.save(librarianRole);

        String signupRequestJson = objectMapper.writeValueAsString(signupRequest);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/register-librarian")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequestJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    public void testThatRegisterAdminReturnsHttp201CreatedWhenValidRegister() throws Exception {
        SignupRequest signupRequest = testDataUtil.createSignupRequestForTest();
        Role adminRole = Role.builder()
                .name("ROLE_ADMIN")
                .build();
        roleRepository.save(adminRole);

        String signupRequestJson = objectMapper.writeValueAsString(signupRequest);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/register-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequestJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    public void testThatRegisterReturnsHttp409ConflictWhenUserAlreadyExists() throws Exception {
        User user = testDataUtil.createUserForTest();
        Role role = testDataUtil.createRoleForTest();
        roleRepository.save(role);
        user.setRole(role);
        userRepository.save(user);

        SignupRequest signupRequest = testDataUtil.createSignupRequestForTest();

        String signupRequestJson = objectMapper.writeValueAsString(signupRequest);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/auth/register-member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequestJson)
        ).andExpect(
                MockMvcResultMatchers.status().isConflict()
        );
    }
}
