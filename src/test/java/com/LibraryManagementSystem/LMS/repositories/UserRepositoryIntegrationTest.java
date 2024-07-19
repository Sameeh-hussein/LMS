package com.LibraryManagementSystem.LMS.repositories;

import com.LibraryManagementSystem.LMS.TestDataUtil;
import com.LibraryManagementSystem.LMS.domain.Role;
import com.LibraryManagementSystem.LMS.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // clean the database after each test
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository underTest;

    @Autowired
    private TestDataUtil testDataUtil;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testThatUserCanBeCreatedAndRecalledByEmail() {
        User user = testDataUtil.createUserForTest();
        Role role = testDataUtil.createRoleForTest();

        roleRepository.save(role);

        user.setRole(role);

        underTest.save(user);

        Optional<User> result = underTest.findByEmail("ali@gmail.com");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(user.getId());
        assertThat(result.get().getUsername()).isEqualTo(user.getUsername());
        assertThat(result.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(result.get().getRole().getName()).isEqualTo(role.getName());
    }

    @Test
    public void testThatUserCanBeCreatedAndRecalledById() {
        User user = testDataUtil.createUserForTest();
        Role role = testDataUtil.createRoleForTest();

        roleRepository.save(role);

        user.setRole(role);

        underTest.save(user);

        Optional<User> result = underTest.findById(user.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(user.getId());
        assertThat(result.get().getUsername()).isEqualTo(user.getUsername());
        assertThat(result.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(result.get().getRole().getName()).isEqualTo(role.getName());
    }

    @Test
    public void testThatUserExistByEmail() {
        User user = testDataUtil.createUserForTest();
        Role role = testDataUtil.createRoleForTest();

        roleRepository.save(role);

        user.setRole(role);

        underTest.save(user);

        Boolean result = underTest.existsByEmail("ali@gmail.com");

        assertThat(result).isTrue();
    }

    @Test
    public void testThatUserCanBeNotExistByEmail() {
        Boolean result = underTest.existsByEmail("notexist@gmail.com");

        assertThat(result).isFalse();
    }
}
