package com.LibraryManagementSystem.LMS;

import com.LibraryManagementSystem.LMS.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TestDataUtil {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TestDataUtil(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User createUserForTest(){
        return User.builder()
                .id(1L)
                .username("Ali-Ahmad")
                .email("ali@gmail.com")
                .password(passwordEncoder.encode("Aa12345"))
                .createdAt(LocalDateTime.now())
                .build();
    }
}
