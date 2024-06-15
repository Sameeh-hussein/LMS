package com.LibraryManagementSystem.LMS.Services;

import com.LibraryManagementSystem.LMS.TestDataUtil;
import com.LibraryManagementSystem.LMS.auth.JwtUtil;
import com.LibraryManagementSystem.LMS.auth.LoginRequest;
import com.LibraryManagementSystem.LMS.auth.SignupRequest;
import com.LibraryManagementSystem.LMS.domain.Role;
import com.LibraryManagementSystem.LMS.domain.User;
import com.LibraryManagementSystem.LMS.exceptions.RoleNotFoundException;
import com.LibraryManagementSystem.LMS.exceptions.UserAlreadyExistsException;
import com.LibraryManagementSystem.LMS.exceptions.UserNotFoundException;
import com.LibraryManagementSystem.LMS.mappers.impl.UserMapper;
import com.LibraryManagementSystem.LMS.repositories.RoleRepository;
import com.LibraryManagementSystem.LMS.repositories.UserRepository;
import com.LibraryManagementSystem.LMS.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl underTest;

    @Autowired
    private TestDataUtil testDataUtil;

    @Test
    public void testThatAuthenticateUserReturnsValidTokenWhenSuccess() {
        User user = testDataUtil.createUserForTest();
        LoginRequest loginRequest = testDataUtil.createLoginRequestForTest();

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
                .thenReturn(true);

        when(jwtUtil.generateToken(user))
                .thenReturn("token");

        String token = underTest.authenticateUser(loginRequest);

        assertNotNull(token);

        assertEquals("token", token);

        verify(userRepository, times(1))
                .findByEmail(user.getEmail());

        verify(passwordEncoder, times(1))
                .matches(TestDataUtil.password, user.getPassword());

        verify(jwtUtil, times(1))
                .generateToken(user);
    }

    @Test
    public void testThatAuthenticateUserThrowUserNotFoundExceptionWhenUserNameNotFound() {
        LoginRequest loginRequest = testDataUtil.createUnValidLoginRequestForTest();

        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () -> {
            underTest.authenticateUser(loginRequest);
        });

        assertNotNull(userNotFoundException);
        assertEquals("User not found with email: " + loginRequest.getEmail(), userNotFoundException.getMessage());

        verify(userRepository, times(1))
                .findByEmail(loginRequest.getEmail());

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtUtil, never())
                .generateToken(any(User.class));
    }

    @Test
    public void testThatAuthenticateUserThrowUserNotFoundExceptionWhenPasswordNotMatch() {
        LoginRequest loginRequest = testDataUtil.createUnValidLoginRequestForTest();
        User user = testDataUtil.createUserForTest();
        loginRequest.setEmail(user.getEmail());

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
                .thenReturn(false);

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () -> {
            underTest.authenticateUser(loginRequest);
        });

        assertNotNull(userNotFoundException);

        assertEquals("Incorrect password", userNotFoundException.getMessage());

        verify(userRepository, times(1))
                .findByEmail(loginRequest.getEmail());

        verify(passwordEncoder, times(1))
                .matches(loginRequest.getPassword(), user.getPassword());

        verify(jwtUtil, never())
                .generateToken(any(User.class));
    }

    @Test
    public void testThatRegisterUserSaveUserWhenSuccess() {
        User user = testDataUtil.createUserForTest();

        SignupRequest signupRequest = testDataUtil.createSignupRequestForTest();

        User userWithoutCreatedAt = testDataUtil.createUserForTestWithoutCreatedAt();

        when(userRepository.existsByEmail(signupRequest.getEmail()))
                .thenReturn(false);

        when(userRepository.existsByUsername(signupRequest.getUsername()))
                .thenReturn(false);

        when(roleRepository.findByName("ROLE_MEMBER"))
                .thenReturn(Optional.of(new Role()));

        when(passwordEncoder.encode(TestDataUtil.password))
                .thenReturn(user.getPassword());

        when(userMapper.mapFrom(signupRequest))
                .thenReturn(userWithoutCreatedAt);

        underTest.registerUserWithRole(signupRequest, "ROLE_MEMBER");

        verify(userRepository, times(1))
                .existsByEmail(signupRequest.getEmail());

        verify(userRepository, times(1))
                .existsByUsername(signupRequest.getUsername());

        verify(roleRepository, times(1))
                .findByName("ROLE_MEMBER");

        verify(passwordEncoder, times(1))
                .encode(signupRequest.getPassword());

        verify(userMapper, times(1))
                .mapFrom(signupRequest);
    }

    @Test
    public void testThatRegisterUserThrowUserAlreadyExistsExceptionWhenEmailExist() {
        SignupRequest signupRequest = testDataUtil.createSignupRequestForTest();

        when(userRepository.existsByEmail(signupRequest.getEmail()))
                .thenReturn(true);

        UserAlreadyExistsException userAlreadyExistsException = assertThrows(UserAlreadyExistsException.class, () -> {
            underTest.registerUserWithRole(signupRequest, "ROLE_MEMBER");
        });

        assertNotNull(userAlreadyExistsException);

        assertEquals("User with email: " + signupRequest.getEmail() + " already exists",
                userAlreadyExistsException.getMessage());

        verify(userRepository, times(1))
                .existsByEmail(signupRequest.getEmail());

        verify(userRepository, never())
                .existsByUsername(anyString());

        verify(roleRepository, never())
                .findByName(anyString());

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userMapper, never())
                .mapFrom(any(SignupRequest.class));
    }

    @Test
    public void testThatRegisterUserThrowUserAlreadyExistsExceptionWhenUserNameExist() {
        SignupRequest signupRequest = testDataUtil.createSignupRequestForTest();

        when(userRepository.existsByEmail(signupRequest.getEmail()))
                .thenReturn(false);

        when(userRepository.existsByUsername(signupRequest.getUsername()))
                .thenReturn(true);

        UserAlreadyExistsException userAlreadyExistsException = assertThrows(UserAlreadyExistsException.class, () -> {
            underTest.registerUserWithRole(signupRequest, "ROLE_MEMBER");
        });

        assertNotNull(userAlreadyExistsException);

        verify(userRepository, times(1))
                .existsByEmail(signupRequest.getEmail());

        verify(userRepository, times(1))
                .existsByUsername(signupRequest.getUsername());

        verify(roleRepository, never())
                .findByName(anyString());

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userMapper, never())
                .mapFrom(any(SignupRequest.class));
    }

    @Test
    public void testThatRegisterUserThrowRoleNotFoundExceptionWhenRoleNotExist() {
        SignupRequest signupRequest = testDataUtil.createSignupRequestForTest();

        when(userRepository.existsByEmail(signupRequest.getEmail()))
                .thenReturn(false);

        when(userRepository.existsByUsername(signupRequest.getUsername()))
                .thenReturn(false);

        when(roleRepository.findByName("ROLE_MEMBER"))
                .thenReturn(Optional.empty());

        RoleNotFoundException roleNotFoundException = assertThrows(RoleNotFoundException.class, () -> {
            underTest.registerUserWithRole(signupRequest, "ROLE_MEMBER");
        });

        assertNotNull(roleNotFoundException);

        verify(userRepository, times(1))
                .existsByEmail(signupRequest.getEmail());

        verify(userRepository, times(1))
                .existsByUsername(signupRequest.getUsername());

        verify(roleRepository, times(1))
                .findByName("ROLE_MEMBER");

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userMapper, never())
                .mapFrom(any(SignupRequest.class));
    }

}
