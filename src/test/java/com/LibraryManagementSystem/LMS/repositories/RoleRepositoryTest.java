package com.LibraryManagementSystem.LMS.repositories;

import com.LibraryManagementSystem.LMS.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    public void setUp() {
        Role role = Role.builder()
                .id(1L)
                .name("ROLE_MEMBER")
                .build();
        roleRepository.save(role);
    }

    @Test
    public void testFindByName() {
        Optional<Role> foundRole = roleRepository.findByName("ROLE_MEMBER");

        assertThat(foundRole.isPresent()).isTrue();
        assertThat(foundRole.get().getName()).isEqualTo("ROLE_MEMBER");
    }

    @Test
    public void testExistsByName() {
        Boolean exists = roleRepository.existsByName("ROLE_MEMBER");

        assertThat(exists).isTrue();
    }
}
