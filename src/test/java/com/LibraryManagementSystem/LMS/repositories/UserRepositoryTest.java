package com.LibraryManagementSystem.LMS.repositories;

import com.LibraryManagementSystem.LMS.domain.Role;
import com.LibraryManagementSystem.LMS.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User user;
    private Role role;

    @BeforeEach
    public void setUp() {
        role = Role.builder()
                .id(1L)
                .name("ROLE_MEMBER")
                .build();
        roleRepository.save(role);

        user = User.builder()
                .id(1L)
                .firstName("Ali")
                .lastName("Ahmad")
                .email("ali@gmail.com")
                .password("123")
                .createdAt(LocalDateTime.now())
                .role(role)
                .build();
        userRepository.save(user);
    }

    @Test
    public void testThatUserCanBeCreatedAndRecalledByEmail() {
        Optional<User> result = userRepository.findByEmail("ali@gmail.com");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(user.getId());
        assertThat(result.get().getUsername()).isEqualTo(user.getUsername());
        assertThat(result.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(result.get().getRole().getName()).isEqualTo(role.getName());
    }

    @Test
    public void testThatUserCanBeCreatedAndRecalledById() {
        Optional<User> result = userRepository.findById(user.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(user.getId());
        assertThat(result.get().getUsername()).isEqualTo(user.getUsername());
        assertThat(result.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(result.get().getRole().getName()).isEqualTo(role.getName());
    }

    @Test
    public void testThatUserExistsByEmail() {
        Boolean result = userRepository.existsByEmail("ali@gmail.com");

        assertThat(result).isTrue();
    }

    @Test
    public void testThatUserDoesNotExistByEmail() {
        Boolean result = userRepository.existsByEmail("notexist@gmail.com");

        assertThat(result).isFalse();
    }
}
