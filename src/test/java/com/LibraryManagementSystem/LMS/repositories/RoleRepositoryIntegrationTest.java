package com.LibraryManagementSystem.LMS.repositories;

import com.LibraryManagementSystem.LMS.TestDataUtil;
import com.LibraryManagementSystem.LMS.domain.Role;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RoleRepositoryIntegrationTest {

    @Autowired
    private RoleRepository underTest;

    @Autowired
    private TestDataUtil testDataUtil;

    @Test
    public void testThatRoleCanBeCreatedAndReturnedByName() {
        Role role = testDataUtil.createRoleForTest();

        underTest.save(role);

        Optional<Role> foundRole = underTest.findByName(role.getName());

        assertThat(foundRole.isPresent()).isTrue();
        assertThat(foundRole.get().getId()).isEqualTo(role.getId());
        assertThat(foundRole.get().getName()).isEqualTo(role.getName());
    }

    @Test
    public void testThatRoleExistByName() {
        Role role = testDataUtil.createRoleForTest();
        underTest.save(role);

        Boolean roleExist = underTest.existsByName(role.getName());

        assertThat(roleExist).isTrue();
    }
}
