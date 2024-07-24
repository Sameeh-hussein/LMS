package com.LibraryManagementSystem.LMS.repositories;

import com.LibraryManagementSystem.LMS.domain.Author;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthorRepositoryTest {
    @Autowired
    private AuthorRepository authorRepository;

    @Test
    public void existsByNameReturnTrueWhenExist() {
        Author author = Author.builder().id(1L).name("Messi").build();

        authorRepository.save(author);

        Boolean exists = authorRepository.existsByName("Messi");

        assertThat(exists).isTrue();
    }

    @Test
    public void existsByNameReturnFalseWhenNotExist() {
        Boolean exists = authorRepository.existsByName("Sami");

        assertThat(exists).isFalse();
    }

    @Test
    public void existsByNameAndIdNotReturnTrueWhenExist() {
        Author author = Author.builder().id(1L).name("Messi").build();
        Author testAuthor = Author.builder().id(2L).name("Di maria").build();
        authorRepository.save(author);
        authorRepository.save(testAuthor);

        Boolean exist = authorRepository.existsByNameAndIdNot("Di maria", 1L);

        assertThat(exist).isTrue();
    }
    
    @Test
    public void existsByNameAndIdNotReturnFalseWhenNotExist() {
        Author author = Author.builder().id(1L).name("Messi").build();
        authorRepository.save(author);

        Boolean exist = authorRepository.existsByNameAndIdNot("Di maria", 1L);

        assertThat(exist).isFalse();
    }

}
