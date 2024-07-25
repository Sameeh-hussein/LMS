package com.LibraryManagementSystem.LMS.controllers;

import com.LibraryManagementSystem.LMS.domain.Book;
import com.LibraryManagementSystem.LMS.domain.Borrow;
import com.LibraryManagementSystem.LMS.domain.Role;
import com.LibraryManagementSystem.LMS.domain.User;
import com.LibraryManagementSystem.LMS.dto.AddBorrowDto;
import com.LibraryManagementSystem.LMS.enums.BorrowStatus;
import com.LibraryManagementSystem.LMS.repositories.BookRepository;
import com.LibraryManagementSystem.LMS.repositories.BorrowRepository;
import com.LibraryManagementSystem.LMS.repositories.RoleRepository;
import com.LibraryManagementSystem.LMS.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class BorrowControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BorrowRepository borrowRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private RoleRepository roleRepository;

    @Test
    @WithMockUser(username = "member", roles = {"MEMBER"})
    public void testThatAddBorrowReturns201CreatedWhenSuccess() throws Exception {
        AddBorrowDto borrowDto = AddBorrowDto.builder()
                .userId(1L)
                .bookId(1L)
                .borrowDate(new Timestamp(System.currentTimeMillis()))
                .returnDate(new Timestamp(System.currentTimeMillis() + 86400000)) // 1 day later
                .build();

        Role role = Role.builder().id(1L).name("ROLE_MEMBER").build();
        User user = User.builder().id(1L).email("member@example.com").role(role).build();
        Book book = Book.builder().id(1L).title("Book Title").build();

        when(roleRepository.save(role)).thenReturn(role);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(borrowRepository.existsByBookIdAndUserId(1L, 1L)).thenReturn(false);

        String borrowJson = objectMapper.writeValueAsString(borrowDto);

        var result = mockMvc.perform(post("/api/borrows")
                .contentType(MediaType.APPLICATION_JSON)
                .content(borrowJson)
        );

        result.andExpect(status().isCreated())
                .andExpect(content().string("Borrow added successfully"));
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatFindAllBorrowsReturns200Ok() throws Exception {
        Borrow borrow = Borrow.builder()
                .id(1L)
                .user(User.builder().id(1L).email("member@example.com").build())
                .book(Book.builder().id(1L).title("Book Title").build())
                .borrowDate(new Timestamp(System.currentTimeMillis()))
                .returnDate(new Timestamp(System.currentTimeMillis() + 86400000))
                .status(BorrowStatus.BORROWED)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Borrow> borrowPage = new PageImpl<>(Collections.singletonList(borrow), pageable, 1);

        when(borrowRepository.findAll(pageable)).thenReturn(borrowPage);

        var result = mockMvc.perform(get("/api/borrows").param("page", "0").param("size", "10"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    @WithMockUser(username = "member", roles = {"MEMBER"})
    public void testThatFindBorrowsByUserIdReturns200Ok() throws Exception {
        Borrow borrow = Borrow.builder()
                .id(1L)
                .user(User.builder().id(1L).email("member@example.com").build())
                .book(Book.builder().id(1L).title("Book Title").build())
                .borrowDate(new Timestamp(System.currentTimeMillis()))
                .returnDate(new Timestamp(System.currentTimeMillis() + 86400000))
                .status(BorrowStatus.BORROWED)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Borrow> borrowPage = new PageImpl<>(Collections.singletonList(borrow), pageable, 1);

        when(borrowRepository.findByUserId(1L, pageable)).thenReturn(borrowPage);

        var result = mockMvc.perform(get("/api/borrows/user/1").param("page", "0").param("size", "10"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatFindBorrowReturns200OkWhenBorrowExists() throws Exception {
        Borrow borrow = Borrow.builder()
                .id(1L)
                .user(User.builder().id(1L).email("member@example.com").build())
                .book(Book.builder().id(1L).title("Book Title").build())
                .borrowDate(new Timestamp(System.currentTimeMillis()))
                .returnDate(new Timestamp(System.currentTimeMillis() + 86400000))
                .status(BorrowStatus.BORROWED)
                .build();

        when(borrowRepository.findById(1L)).thenReturn(Optional.of(borrow));

        var result = mockMvc.perform(get("/api/borrows/1"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatFindBorrowReturns404NotFoundWhenBorrowNotExists() throws Exception {
        when(borrowRepository.findById(999L)).thenReturn(Optional.empty());

        var result = mockMvc.perform(get("/api/borrows/999"));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void testThatReturnBookReturns200OkWhenSuccess() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("member@example.com", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_MEMBER")))
        );

        User user = User.builder().id(1L).email("member@example.com").build();
        Borrow borrow = Borrow.builder()
                .id(1L)
                .user(user)
                .book(Book.builder().id(1L).title("Book Title").build())
                .borrowDate(new Timestamp(System.currentTimeMillis()))
                .returnDate(new Timestamp(System.currentTimeMillis() + 86400000))
                .status(BorrowStatus.BORROWED)
                .build();

        when(borrowRepository.findById(1L)).thenReturn(Optional.of(borrow));
        when(userRepository.findByEmail("member@example.com")).thenReturn(Optional.of(user));

        var result = mockMvc.perform(patch("/api/borrows/1/returned"));

        result.andExpect(status().isOk())
                .andExpect(content().string("Borrow approved successfully"));
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatDeleteBorrowReturns200OkWhenBorrowExists() throws Exception {
        Borrow borrow = Borrow.builder().id(1L).build();

        when(borrowRepository.findById(1L)).thenReturn(Optional.of(borrow));

        var result = mockMvc.perform(delete("/api/borrows/1"));

        result.andExpect(status().isOk())
                .andExpect(content().string("Borrow deleted successfully"));
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatDeleteBorrowReturns404NotFoundWhenBorrowNotExists() throws Exception {
        when(borrowRepository.findById(999L)).thenReturn(Optional.empty());

        var result = mockMvc.perform(delete("/api/borrows/999"));

        result.andExpect(status().isNotFound());
    }
}
