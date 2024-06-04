package com.LibraryManagementSystem.LMS.repositories;

import com.LibraryManagementSystem.LMS.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);
}
