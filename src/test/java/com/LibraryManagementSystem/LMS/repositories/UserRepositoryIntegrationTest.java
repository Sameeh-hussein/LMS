package com.LibraryManagementSystem.LMS.repositories;

import com.LibraryManagementSystem.LMS.TestDataUtil;
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

    private final UserRepository underTest;
    private final TestDataUtil testDataUtil;

    @Autowired
    public UserRepositoryIntegrationTest(UserRepository underTest, TestDataUtil testDataUtil) {
        this.underTest = underTest;
        this.testDataUtil = testDataUtil;
    }

    @Test
    public void testThatUserCanBeCreatedAndRecalledByEmail() {
        User user = testDataUtil.createUserForTest();

        underTest.save(user);

        Optional<User> result = underTest.findByEmail("ali@gmail.com");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(user.getId());
        assertThat(result.get().getUsername()).isEqualTo(user.getUsername());
        assertThat(result.get().getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    public void testThatUserExistByEmail() {
        User user = testDataUtil.createUserForTest();

        underTest.save(user);

        Boolean result = underTest.existsByEmail("ali@gmail.com");

        assertThat(result).isTrue();
    }

    @Test
    public void testThatUserExistByUserName() {
        User user = testDataUtil.createUserForTest();

        underTest.save(user);

        Boolean result = underTest.existsByUsername("Ali-Ahmad");

        assertThat(result).isTrue();
    }

    @Test
    public void testThatUserCanBeNotExistByEmail() {
        Boolean result = underTest.existsByEmail("notexist@gmail.com");

        assertThat(result).isFalse();
    }
}
