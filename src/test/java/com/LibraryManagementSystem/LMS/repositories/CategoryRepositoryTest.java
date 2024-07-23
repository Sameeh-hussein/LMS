package com.LibraryManagementSystem.LMS.repositories;

import com.LibraryManagementSystem.LMS.domain.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void existsByNameReturnTrueWhenExist() {
        Category category = Category.builder().id(1L).name("History").build();

        categoryRepository.save(category);

        Boolean exists = categoryRepository.existsByName("History");

        assertThat(exists).isTrue();
    }

    @Test
    public void existsByNameReturnFalseWhenNotExist() {
        Boolean exists = categoryRepository.existsByName("Art");

        assertThat(exists).isFalse();
    }
}
