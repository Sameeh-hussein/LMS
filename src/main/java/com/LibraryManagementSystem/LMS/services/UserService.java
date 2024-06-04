package com.LibraryManagementSystem.LMS.services;

import com.LibraryManagementSystem.LMS.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    void save(User user);

    Optional<User> findByEmail(String email);
}
