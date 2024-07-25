package com.LibraryManagementSystem.LMS.repositories;

import com.LibraryManagementSystem.LMS.domain.*;
import com.LibraryManagementSystem.LMS.enums.BorrowStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BorrowRepositoryTest {

    @Autowired
    private BorrowRepository borrowRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        Role role = Role.builder()
                .id(1L)
                .name("ROLE_MEMBER")
                .build();
        roleRepository.save(role);

        User user = User.builder()
                .id(1L)
                .firstName("Ali")
                .lastName("Ahmad")
                .email("ali@gmail.com")
                .password("123")
                .createdAt(LocalDateTime.now())
                .role(role)
                .build();
        userRepository.save(user);

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

        Borrow borrow = Borrow.builder()
                .user(user)
                .book(book)
                .borrowDate(new Timestamp(System.currentTimeMillis()))
                .returnDate(new Timestamp(System.currentTimeMillis() + 86400000))
                .status(BorrowStatus.BORROWED)
                .build();

        borrowRepository.save(borrow);
    }

    @Test
    void testExistsByBookIdAndUserIdReturnTrueWhenExist() {
        Boolean exists = borrowRepository.existsByBookIdAndUserId(1L, 1L);
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByBookIdAndUserIdReturnFalseWhenNotExist() {
        Boolean exists = borrowRepository.existsByBookIdAndUserId(1L, 2L);
        assertThat(exists).isFalse();
    }

    @Test
    void testFindByStatusAndReturnDateBefore() {
        List<Borrow> borrows = borrowRepository.findByStatusAndReturnDateBefore(BorrowStatus.BORROWED, new Timestamp(System.currentTimeMillis() + 172800000));
        assertThat(borrows).hasSize(1);
    }

    @Test
    void testFindByUserId() {
        Page<Borrow> borrows = borrowRepository.findByUserId(1L, Pageable.unpaged());
        assertThat(borrows.getTotalElements()).isEqualTo(1);
    }
}
