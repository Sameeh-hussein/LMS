package com.LibraryManagementSystem.LMS.controllers;

import com.LibraryManagementSystem.LMS.TestDataUtil;
import com.LibraryManagementSystem.LMS.auth.LoginRequest;
import com.LibraryManagementSystem.LMS.auth.SignupRequest;
import com.LibraryManagementSystem.LMS.domain.User;
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
public class UserControllerTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private final TestDataUtil testDataUtil;

    private final UserRepository userRepository;

    @Autowired
    public UserControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, TestDataUtil testDataUtil, UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.testDataUtil = testDataUtil;
        this.userRepository = userRepository;
    }

    @Test
    public void testThatLoginReturnsHttp200OkWhenUserExist() throws Exception {
        User user = testDataUtil.createUserForTest();

        userRepository.save(user);

        LoginRequest loginRequest = testDataUtil.createLoginRequestForTest();

        String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/login")
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
                MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testThatSignupReturnsHttp201CreatedWhenValidRegister() throws Exception {
        SignupRequest signupRequest = testDataUtil.createSignupRequestForTest();

        String signupRequestJson = objectMapper.writeValueAsString(signupRequest);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signupRequestJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    public void testThatSignupReturnsHttp409ConflictWhenUnValidRegister() throws Exception {
        User user = testDataUtil.createUserForTest();

        userRepository.save(user);

        SignupRequest signupRequest = testDataUtil.createSignupRequestForTest();

        String signupRequestJson = objectMapper.writeValueAsString(signupRequest);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequestJson)
        ).andExpect(
                MockMvcResultMatchers.status().isConflict()
        );
    }

}
