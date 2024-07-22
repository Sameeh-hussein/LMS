package com.LibraryManagementSystem.LMS.controllers;

import com.LibraryManagementSystem.LMS.TestDataUtil;
import com.LibraryManagementSystem.LMS.auth.JwtUtil;
import com.LibraryManagementSystem.LMS.auth.LoginRequest;
import com.LibraryManagementSystem.LMS.auth.SignupRequest;
import com.LibraryManagementSystem.LMS.controller.AuthController;
import com.LibraryManagementSystem.LMS.domain.Role;
import com.LibraryManagementSystem.LMS.domain.User;
import com.LibraryManagementSystem.LMS.exceptions.UserAlreadyExistsException;
import com.LibraryManagementSystem.LMS.exceptions.UserNotFoundException;
import com.LibraryManagementSystem.LMS.repositories.RoleRepository;
import com.LibraryManagementSystem.LMS.repositories.UserRepository;
import com.LibraryManagementSystem.LMS.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @Before("")
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void testThatLoginReturnsHttp200OkWhenUserExist() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder().email("messi@gmail.com").password("LM10").build();
        User user = User.builder().email("messi@gmail.com").build();
        String userJson = objectMapper.writeValueAsString(loginRequest);


        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("messi@gmail.com", "LM10"));
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(userService.authenticateUser(any(LoginRequest.class))).thenReturn("token");
        when(jwtUtil.generateToken(any(User.class))).thenReturn("token");

        var result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson)
        );

        result.andExpect(status().isOk())
                .andExpect(content().string("token"));
    }

    @Test
    public void testThatLoginReturnsHttp404NotFoundWhenUserNotExist() throws Exception {
        LoginRequest loginRequest = testDataUtil.createLoginRequestForTest();
        String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

        // Mock the behavior of the UserService to throw UserNotFoundException
        when(userService.authenticateUser(any(LoginRequest.class)))
                .thenThrow(new UserNotFoundException("User not found with email: " + loginRequest.getEmail()));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testThatRegisterMemberReturnsHttp201CreatedWhenValidRegister() throws Exception {
        SignupRequest signupRequest = testDataUtil.createSignupRequestForTest();
        Role memberRole = Role.builder().name("ROLE_MEMBER").build();

        when(roleRepository.save(any(Role.class))).thenReturn(memberRole);
        when(userRepository.save(any(User.class))).thenReturn(new User());

        String signupRequestJson = objectMapper.writeValueAsString(signupRequest);

        mockMvc.perform(post("/api/auth/register-member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequestJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testThatRegisterLibrarianReturnsHttp201CreatedWhenValidRegister() throws Exception {
        SignupRequest signupRequest = testDataUtil.createSignupRequestForTest();
        Role librarianRole = Role.builder().name("ROLE_LIBRARIAN").build();

        when(roleRepository.save(any(Role.class))).thenReturn(librarianRole);
        when(userRepository.save(any(User.class))).thenReturn(new User());

        String signupRequestJson = objectMapper.writeValueAsString(signupRequest);

        mockMvc.perform(post("/api/auth/register-librarian")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequestJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testThatRegisterAdminReturnsHttp201CreatedWhenValidRegister() throws Exception {
        SignupRequest signupRequest = testDataUtil.createSignupRequestForTest();
        Role adminRole = Role.builder().name("ROLE_ADMIN").build();

        when(roleRepository.save(any(Role.class))).thenReturn(adminRole);
        when(userRepository.save(any(User.class))).thenReturn(new User());

        String signupRequestJson = objectMapper.writeValueAsString(signupRequest);

        mockMvc.perform(post("/api/auth/register-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequestJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testThatRegisterReturnsHttp409ConflictWhenUserAlreadyExists() throws Exception {
        SignupRequest signupRequest = SignupRequest.builder()
                .email("messi@gmail.com")
                .firstName("lio")
                .lastName("messi")
                .password("LM1045")
                .build();

        String requestJson = objectMapper.writeValueAsString(signupRequest);

        doThrow(new UserAlreadyExistsException("User already exists")).when(userService).registerUserWithRole(any(SignupRequest.class), eq("ROLE_MEMBER"));

        mockMvc.perform(post("/api/auth/register-member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict());
    }
}
