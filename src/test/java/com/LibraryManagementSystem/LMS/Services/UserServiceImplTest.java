package com.LibraryManagementSystem.LMS.Services;

import com.LibraryManagementSystem.LMS.TestDataUtil;
import com.LibraryManagementSystem.LMS.auth.JwtUtil;
import com.LibraryManagementSystem.LMS.auth.LoginRequest;
import com.LibraryManagementSystem.LMS.auth.SignupRequest;
import com.LibraryManagementSystem.LMS.auth.UpdateDataRequest;
import com.LibraryManagementSystem.LMS.domain.Role;
import com.LibraryManagementSystem.LMS.domain.User;
import com.LibraryManagementSystem.LMS.dto.ReturnUserDto;
import com.LibraryManagementSystem.LMS.exceptions.InvalidPasswordException;
import com.LibraryManagementSystem.LMS.exceptions.RoleNotFoundException;
import com.LibraryManagementSystem.LMS.exceptions.UserAlreadyExistsException;
import com.LibraryManagementSystem.LMS.exceptions.UserNotFoundException;
import com.LibraryManagementSystem.LMS.mappers.impl.UserRequestMapper;
import com.LibraryManagementSystem.LMS.mappers.impl.UserReturnMapper;
import com.LibraryManagementSystem.LMS.repositories.RoleRepository;
import com.LibraryManagementSystem.LMS.repositories.UserRepository;
import com.LibraryManagementSystem.LMS.services.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
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
    private UserRequestMapper userRequestMapper;

    @Mock
    private UserReturnMapper userReturnMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl underTest;

    @Autowired
    private TestDataUtil testDataUtil;

    private void setUpSecurityContext(String authenticatedEmail) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(authenticatedEmail);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testThatAuthenticateUserReturnsValidTokenWhenSuccess() {
        User user = testDataUtil.createUserForTest();
        LoginRequest loginRequest = testDataUtil.createLoginRequestForTest();

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken(user))
                .thenReturn("token");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        String token = underTest.authenticateUser(loginRequest);

        assertNotNull(token);

        assertEquals("token", token);

        verify(userRepository, times(1))
                .findByEmail(user.getEmail());

        verify(jwtUtil, times(1))
                .generateToken(user);

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void testThatAuthenticateUserThrowUserNotFoundExceptionWhenUserNotFound() {
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

        verify(jwtUtil, never())
                .generateToken(any(User.class));
    }

    @Test
    public void testThatAuthenticateUserThrowsBadCredentialsExceptionWhenPasswordDoesNotMatch() {
        LoginRequest loginRequest = testDataUtil.createLoginRequestForTest();
        User user = testDataUtil.createUserForTest();
        loginRequest.setEmail(user.getEmail());

        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Password does not match"));

        BadCredentialsException badCredentialsException = assertThrows(BadCredentialsException.class, () -> {
            underTest.authenticateUser(loginRequest);
        });

        assertNotNull(badCredentialsException);
        assertEquals("Password does not match", badCredentialsException.getMessage());

        verify(userRepository, never()).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(any(User.class));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void testThatRegisterUserSaveUserWhenSuccess() {
        User user = testDataUtil.createUserForTest();

        SignupRequest signupRequest = testDataUtil.createSignupRequestForTest();

        User userWithoutCreatedAt = testDataUtil.createUserForTestWithoutCreatedAt();

        when(userRepository.existsByEmail(signupRequest.getEmail()))
                .thenReturn(false);

        when(roleRepository.findByName("ROLE_MEMBER"))
                .thenReturn(Optional.of(new Role()));

        when(passwordEncoder.encode(TestDataUtil.password))
                .thenReturn(user.getPassword());

        when(userRequestMapper.mapFrom(signupRequest))
                .thenReturn(userWithoutCreatedAt);

        underTest.registerUserWithRole(signupRequest, "ROLE_MEMBER");

        verify(userRepository, times(1))
                .existsByEmail(signupRequest.getEmail());

        verify(roleRepository, times(1))
                .findByName("ROLE_MEMBER");

        verify(passwordEncoder, times(1))
                .encode(signupRequest.getPassword());

        verify(userRequestMapper, times(1))
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

        verify(roleRepository, never())
                .findByName(anyString());

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRequestMapper, never())
                .mapFrom(any(SignupRequest.class));
    }

    @Test
    public void testThatRegisterUserThrowRoleNotFoundExceptionWhenRoleNotExist() {
        SignupRequest signupRequest = testDataUtil.createSignupRequestForTest();

        when(userRepository.existsByEmail(signupRequest.getEmail()))
                .thenReturn(false);

        when(roleRepository.findByName("ROLE_MEMBER"))
                .thenReturn(Optional.empty());

        RoleNotFoundException roleNotFoundException = assertThrows(RoleNotFoundException.class, () -> {
            underTest.registerUserWithRole(signupRequest, "ROLE_MEMBER");
        });

        assertNotNull(roleNotFoundException);

        verify(userRepository, times(1))
                .existsByEmail(signupRequest.getEmail());

        verify(roleRepository, times(1))
                .findByName("ROLE_MEMBER");

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRequestMapper, never())
                .mapFrom(any(SignupRequest.class));
    }

    @Test
    public void testThatFindAllUsersReturnAllTheUsers() {
        Role role = testDataUtil.createRoleForTest();

        User user = testDataUtil.createUserForTest();
        User user1 = testDataUtil.createUserForTest1();
        List<User> users = Arrays.asList(user, user1);

        user.setRole(role);
        user1.setRole(role);

        when(userRepository.findAll()).thenReturn(users);
        when(userReturnMapper.mapTo(user)).thenReturn(new ReturnUserDto());
        when(userReturnMapper.mapTo(user1)).thenReturn(new ReturnUserDto());

        List<ReturnUserDto> result = underTest.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testThatFindUserByIdReturnTheUserWhenUserExists() {
        User user = testDataUtil.createUserForTest();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userReturnMapper.mapTo(user)).thenReturn(new ReturnUserDto());

        ReturnUserDto result = underTest.findUserById(user.getId());

        assertNotNull(result);
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    public void testThatFindUserByIdThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () -> {
            underTest.findUserById(99L);
        });

        assertNotNull(userNotFoundException);
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    public void testThatUpdateUserDataUpdateSpecificUserData() {
        SignupRequest signupRequest = SignupRequest.builder()
                .email("newemail@gmail.com")
                .firstName("newfirstname")
                .lastName("newlastname")
                .password("newpassword")
                .build();

        User user = testDataUtil.createUserForTest();
        String authenticatedEmail = user.getEmail();
        setUpSecurityContext(authenticatedEmail);

        UpdateDataRequest request = UpdateDataRequest.builder()
                .password(TestDataUtil.password)
                .data(signupRequest)
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TestDataUtil.password, user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newpassword")).thenReturn("newpasswordencoded");

        underTest.updateUserData(user.getId(), request);

        assertEquals("newemail@gmail.com", user.getEmail());
        assertEquals("newfirstname", user.getFirstName());
        assertEquals("newlastname", user.getLastName());
        assertEquals("newpasswordencoded", user.getPassword());

        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).findByEmail(authenticatedEmail);
        verify(passwordEncoder, times(1)).encode(request.getData().getPassword());
    }

    @Test
    public void testThatUpdateUserThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        setUpSecurityContext("not@exist.com");

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserNotFoundException userNotFoundException = assertThrows(UserNotFoundException.class, () -> {
            underTest.updateUserData(99L, new UpdateDataRequest());
        });

        assertNotNull(userNotFoundException);
        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).findByEmail(anyString());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testThatUpdateUserThrowsInvalidPasswordExceptionWhenPasswordDoesNotMatch() {
        String incorrectPassword = "wrongpassword";
        UpdateDataRequest updateDataRequest = UpdateDataRequest.builder()
                .password(incorrectPassword)
                .data(SignupRequest.builder()
                        .email("newemail@gmail.com")
                        .firstName("newfirstname")
                        .lastName("newlastname")
                        .password("newpassword")
                        .build())
                .build();

        User user = testDataUtil.createUserForTest1();
        String authenticatedEmail = user.getEmail();
        setUpSecurityContext(authenticatedEmail);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(authenticatedEmail)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(incorrectPassword, user.getPassword())).thenReturn(false);

        InvalidPasswordException invalidPasswordException = assertThrows(InvalidPasswordException.class, () -> {
            underTest.updateUserData(user.getId(), updateDataRequest);
        });

        assertNotNull(invalidPasswordException);
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).findByEmail(authenticatedEmail);
        verify(passwordEncoder, times(1)).matches(incorrectPassword, user.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testThatUpdateUserThrowsAccessDeniedExceptionWhenCurrentUserIsDifferent() {
        User userToUpdate = testDataUtil.createUserForTest();
        User currentUser = testDataUtil.createUserForTest1(); // Different user
        setUpSecurityContext(currentUser.getEmail());

        UpdateDataRequest updateDataRequest = UpdateDataRequest.builder()
                .password(TestDataUtil.password)
                .data(SignupRequest.builder()
                        .email("newemail@gmail.com")
                        .firstName("newfirstname")
                        .lastName("newlastname")
                        .password("newpassword")
                        .build())
                .build();

        when(userRepository.findById(userToUpdate.getId())).thenReturn(Optional.of(userToUpdate));
        when(userRepository.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class, () -> {
            underTest.updateUserData(userToUpdate.getId(), updateDataRequest);
        });

        assertNotNull(accessDeniedException);
        verify(userRepository, times(1)).findById(userToUpdate.getId());
        verify(userRepository, times(1)).findByEmail(currentUser.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }
}

