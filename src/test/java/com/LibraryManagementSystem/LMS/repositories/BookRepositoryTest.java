package com.LibraryManagementSystem.LMS.repositories;

import com.LibraryManagementSystem.LMS.domain.Author;
import com.LibraryManagementSystem.LMS.domain.Book;
import com.LibraryManagementSystem.LMS.domain.Category;
import org.junit.jupiter.api.BeforeEach;
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
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    public void setUp() {
        Category category = Category.builder().id(1L).name("History").build();
        categoryRepository.save(category);

        Author author = Author.builder().id(1L).name("Messi").build();
        authorRepository.save(author);

        Book book = Book.builder()
                .id(1L)
                .title("Book")
                .isbn("123")
                .publicationYear("2010")
                .category(category)
                .authors(List.of(author))
                .build();
        bookRepository.save(book);
    }

    @Test
    public void existByIsbnReturnsTrueWhenExist() {
        Boolean exist = bookRepository.existsByIsbn("123");

        assertThat(exist).isTrue();
    }

    @Test
    public void existByIsbnReturnsTrueWhenNotExist() {
        Boolean exist = bookRepository.existsByIsbn("1234");

        assertThat(exist).isFalse();
    }

    @Test
    public void existByTitleReturnsTrueWhenNExist() {
        Boolean exist = bookRepository.existsByTitle("Book");

        assertThat(exist).isTrue();
    }

    @Test
    public void existByTitleReturnsTrueWhenNotExist() {
        Boolean exist = bookRepository.existsByTitle("notexist");

        assertThat(exist).isFalse();
    }

    @Test
    public void existsByIsbnAndIdNotReturnTrueWhenExist() {
        Category category1 = Category.builder().id(2L).name("Art").build();
        categoryRepository.save(category1);

        Author author1 = Author.builder().id(2L).name("Di Maria").build();
        authorRepository.save(author1);

        Book book1 = Book.builder()
                .id(2L)
                .title("Book")
                .isbn("1234")
                .publicationYear("2010")
                .category(category1)
                .authors(List.of(author1))
                .build();
        bookRepository.save(book1);

        Boolean exist = bookRepository.existsByIsbnAndIdNot("1234", 1L);

        assertThat(exist).isTrue();
    }

    @Test
    public void existsByIsbnAndIdNotReturnFalseWhenNotExist() {
        Boolean exist = bookRepository.existsByIsbnAndIdNot("123", 1L);

        assertThat(exist).isFalse();
    }

    @Test
    public void existsByTitleAndIdNotReturnTrueWhenExist() {
        Category category1 = Category.builder().id(2L).name("Art").build();
        categoryRepository.save(category1);

        Author author1 = Author.builder().id(2L).name("Di Maria").build();
        authorRepository.save(author1);

        Book book1 = Book.builder()
                .id(2L)
                .title("Book")
                .isbn("1234")
                .publicationYear("2010")
                .category(category1)
                .authors(List.of(author1))
                .build();
        bookRepository.save(book1);

        Boolean exist = bookRepository.existsByTitleAndIdNot("Book", 1L);

        assertThat(exist).isTrue();
    }

    @Test
    public void existsByTitleAndIdNotReturnFalseWhenNotExist() {
        Boolean exist = bookRepository.existsByTitleAndIdNot("Book", 1L);

        assertThat(exist).isFalse();
    }
}
