package com.LibraryManagementSystem.LMS.Services;

import com.LibraryManagementSystem.LMS.domain.Book;
import com.LibraryManagementSystem.LMS.domain.Borrow;
import com.LibraryManagementSystem.LMS.domain.Role;
import com.LibraryManagementSystem.LMS.domain.User;
import com.LibraryManagementSystem.LMS.dto.AddBorrowDto;
import com.LibraryManagementSystem.LMS.dto.ReturnBookDto;
import com.LibraryManagementSystem.LMS.dto.ReturnBorrowDto;
import com.LibraryManagementSystem.LMS.dto.ReturnUserDto;
import com.LibraryManagementSystem.LMS.enums.BorrowStatus;
import com.LibraryManagementSystem.LMS.exceptions.*;
import com.LibraryManagementSystem.LMS.mappers.impl.BookReturnMapper;
import com.LibraryManagementSystem.LMS.mappers.impl.UserReturnMapper;
import com.LibraryManagementSystem.LMS.repositories.BookRepository;
import com.LibraryManagementSystem.LMS.repositories.BorrowRepository;
import com.LibraryManagementSystem.LMS.repositories.UserRepository;
import com.LibraryManagementSystem.LMS.services.impl.BorrowServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.DirtiesContext;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BorrowServiceTest {

    @Mock
    private BorrowRepository borrowRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookReturnMapper bookReturnMapper;

    @Mock
    private UserReturnMapper userReturnMapper;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private BorrowServiceImpl underTest;

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testAddBorrowSuccess() {
        AddBorrowDto request = AddBorrowDto.builder()
                .userId(1L)
                .bookId(1L)
                .borrowDate(new Timestamp(System.currentTimeMillis()))
                .returnDate(new Timestamp(System.currentTimeMillis() + 86400000)) // 1 day later
                .build();

        User user = User.builder().id(1L).role(Role.builder().name("ROLE_MEMBER").build()).build();
        Book book = Book.builder().id(1L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(borrowRepository.existsByBookIdAndUserId(1L, 1L)).thenReturn(false);

        underTest.addBorrow(request);

        verify(borrowRepository, times(1)).save(any(Borrow.class));
    }

    @Test
    public void testAddBorrowUserNotFound() {
        AddBorrowDto request = AddBorrowDto.builder()
                .userId(1L)
                .bookId(1L)
                .borrowDate(new Timestamp(System.currentTimeMillis()))
                .returnDate(new Timestamp(System.currentTimeMillis() + 86400000))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> underTest.addBorrow(request));

        assertEquals("User with id: 1 not found", exception.getMessage());
    }

    @Test
    public void testAddBorrowBookNotFound() {
        AddBorrowDto request = AddBorrowDto.builder()
                .userId(1L)
                .bookId(1L)
                .borrowDate(new Timestamp(System.currentTimeMillis()))
                .returnDate(new Timestamp(System.currentTimeMillis() + 86400000))
                .build();

        User user = User.builder().id(1L).role(Role.builder().name("ROLE_MEMBER").build()).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> underTest.addBorrow(request));

        assertEquals("Book with id: 1 not found", exception.getMessage());
    }

    @Test
    public void testAddBorrowAlreadyExist() {
        AddBorrowDto request = AddBorrowDto.builder()
                .userId(1L)
                .bookId(1L)
                .borrowDate(new Timestamp(System.currentTimeMillis()))
                .returnDate(new Timestamp(System.currentTimeMillis() + 86400000))
                .build();

        User user = User.builder().id(1L).role(Role.builder().name("ROLE_MEMBER").build()).build();
        Book book = Book.builder().id(1L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(borrowRepository.existsByBookIdAndUserId(1L, 1L)).thenReturn(true);

        BorrowAlreadyExistException exception = assertThrows(BorrowAlreadyExistException.class, () -> underTest.addBorrow(request));

        assertEquals("User with id: 1 already borrowed the book with id: 1", exception.getMessage());
    }

    @Test
    public void testSetBorrowStatusReturnedSuccess() {
        Borrow borrow = Borrow.builder()
                .id(1L)
                .user(User.builder().id(1L).email("test@example.com").build())
                .status(BorrowStatus.BORROWED)
                .build();

        when(borrowRepository.findById(1L)).thenReturn(Optional.of(borrow));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(borrow.getUser()));
        when(authentication.getName()).thenReturn("test@example.com");

        underTest.setBorrowStatusReturned(1L);

        assertEquals(BorrowStatus.RETURNED, borrow.getStatus(), "The borrow status should be updated to RETURNED");
        verify(borrowRepository, times(1)).save(argThat(b -> BorrowStatus.RETURNED.equals(b.getStatus())));
    }

    @Test
    public void testSetBorrowStatusReturnedUnauthorized() {
        Borrow borrow = Borrow.builder()
                .id(1L)
                .user(User.builder().id(1L).email("test@example.com").build())
                .status(BorrowStatus.BORROWED)
                .build();

        User anotherUser = User.builder().id(2L).email("another@example.com").build();

        when(borrowRepository.findById(1L)).thenReturn(Optional.of(borrow));
        when(userRepository.findByEmail("another@example.com")).thenReturn(Optional.of(anotherUser));
        when(authentication.getName()).thenReturn("another@example.com");

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> underTest.setBorrowStatusReturned(1L));

        assertEquals("You are not authorized to return this book", exception.getMessage());
    }

    @Test
    public void testSetBorrowStatusReturnedAlreadyReturned() {
        Borrow borrow = Borrow.builder()
                .id(1L)
                .user(User.builder().id(1L).email("test@example.com").build())
                .status(BorrowStatus.RETURNED)
                .build();

        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("test@example.com");
        when(borrowRepository.findById(1L)).thenReturn(Optional.of(borrow));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(borrow.getUser()));

        AlreadyReturnedException exception = assertThrows(AlreadyReturnedException.class, () -> underTest.setBorrowStatusReturned(1L));

        assertEquals("You already returned the book", exception.getMessage());
    }


    @Test
    public void testUpdateOverdueBorrows() {
        Borrow borrow = Borrow.builder()
                .id(1L)
                .status(BorrowStatus.BORROWED)
                .returnDate(new Timestamp(System.currentTimeMillis() - 86400000)) // 1 day overdue
                .build();

        when(borrowRepository.findByStatusAndReturnDateBefore(eq(BorrowStatus.BORROWED), any(Timestamp.class)))
                .thenReturn(List.of(borrow));

        underTest.updateOverdueBorrows();

        assertEquals(BorrowStatus.OVERDUE, borrow.getStatus());
        verify(borrowRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void testFindAllBorrows() {
        Borrow borrow = Borrow.builder()
                .id(1L)
                .user(User.builder().id(1L).build())
                .book(Book.builder().id(1L).build())
                .status(BorrowStatus.BORROWED)
                .build();

        ReturnBorrowDto returnBorrowDto = ReturnBorrowDto.builder()
                .id(1L)
                .user(ReturnUserDto.builder().id(1L).build())
                .book(ReturnBookDto.builder().id(1L).build())
                .status(BorrowStatus.BORROWED)
                .build();

        Page<Borrow> borrows = new PageImpl<>(List.of(borrow));
        when(borrowRepository.findAll(PageRequest.of(0, 5))).thenReturn(borrows);
        when(userReturnMapper.mapTo(any(User.class))).thenReturn(ReturnUserDto.builder().id(1L).build());
        when(bookReturnMapper.mapTo(any(Book.class))).thenReturn(ReturnBookDto.builder().id(1L).build());

        Page<ReturnBorrowDto> result = underTest.findAllBorrows(PageRequest.of(0, 5));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(returnBorrowDto.getId(), result.getContent().get(0).getId());
    }

    @Test
    public void testFindBorrowById() {
        Borrow borrow = Borrow.builder()
                .id(1L)
                .user(User.builder().id(1L).build())
                .book(Book.builder().id(1L).build())
                .status(BorrowStatus.BORROWED)
                .build();

        ReturnBorrowDto returnBorrowDto = ReturnBorrowDto.builder()
                .id(1L)
                .user(ReturnUserDto.builder().id(1L).build())
                .book(ReturnBookDto.builder().id(1L).build())
                .status(BorrowStatus.BORROWED)
                .build();

        when(borrowRepository.findById(1L)).thenReturn(Optional.of(borrow));
        when(userReturnMapper.mapTo(any(User.class))).thenReturn(ReturnUserDto.builder().id(1L).build());
        when(bookReturnMapper.mapTo(any(Book.class))).thenReturn(ReturnBookDto.builder().id(1L).build());

        ReturnBorrowDto result = underTest.findBorrowById(1L);

        assertNotNull(result);
        assertEquals(returnBorrowDto.getId(), result.getId());
    }

    @Test
    public void testFindBorrowByIdNotFound() {
        when(borrowRepository.findById(1L)).thenReturn(Optional.empty());

        BorrowNotFoundException exception = assertThrows(BorrowNotFoundException.class, () -> underTest.findBorrowById(1L));

        assertNotNull(exception);
        assertEquals("Borrow with id: 1 not found", exception.getMessage());
    }
}
