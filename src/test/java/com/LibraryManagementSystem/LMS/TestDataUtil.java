package com.LibraryManagementSystem.LMS;

import com.LibraryManagementSystem.LMS.auth.LoginRequest;
import com.LibraryManagementSystem.LMS.auth.SignupRequest;
import com.LibraryManagementSystem.LMS.domain.Role;
import com.LibraryManagementSystem.LMS.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TestDataUtil {

    private final PasswordEncoder passwordEncoder;

    public static String password = "Aa12345";

    public TestDataUtil(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User createUserForTest() {
        return User.builder()
                .id(1L)
                .username("Ali-Ahmad")
                .email("ali@gmail.com")
                .password(passwordEncoder.encode(password))
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Role createRoleForTest() {
        return Role.builder()
                .id(1L)
                .name("ROLE_MEMBER")
                .build();
    }

    public User createUserForTestWithoutCreatedAt() {
        return User.builder()
                .id(1L)
                .username("Ali-Ahmad")
                .email("ali@gmail.com")
                .password(password)
                .build();
    }

    public LoginRequest createLoginRequestForTest() {
        return LoginRequest.builder()
                .email("ali@gmail.com")
                .password(password)
                .build();
    }

    public LoginRequest createUnValidLoginRequestForTest() {
        return LoginRequest.builder()
                .email("notexist@gmail.com")
                .password("notexist")
                .build();
    }

    public SignupRequest createSignupRequestForTest() {
        return SignupRequest.builder()
                .email("ali@gmail.com")
                .username("Ali-Ahmad")
                .password(password)
                .build();
    }
}
