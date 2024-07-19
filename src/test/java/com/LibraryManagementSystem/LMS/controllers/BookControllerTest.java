package com.LibraryManagementSystem.LMS.controllers;

import com.LibraryManagementSystem.LMS.domain.Author;
import com.LibraryManagementSystem.LMS.domain.Book;
import com.LibraryManagementSystem.LMS.domain.Category;
import com.LibraryManagementSystem.LMS.dto.AddBookDto;
import com.LibraryManagementSystem.LMS.repositories.AuthorRepository;
import com.LibraryManagementSystem.LMS.repositories.BookRepository;
import com.LibraryManagementSystem.LMS.repositories.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private CategoryRepository categoryRepository;

    @Test
    public void testThatFindAllBooksReturns200Ok() throws Exception {
        Category category = Category.builder().id(1L).name("Art").build();
        Author author = Author.builder().id(1L).name("Messi").build();
        Book book1 = Book.builder()
                .id(1L)
                .title("Book1")
                .isbn("123456789")
                .publicationYear("2010")
                .category(category)
                .authors(List.of(author))
                .build();

        Book book2 = Book.builder()
                .id(2L)
                .title("Book2")
                .isbn("123456785")
                .publicationYear("2011")
                .category(category)
                .authors(List.of(author))
                .build();

        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        var result = mockMvc.perform(get("/api/Books"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Book1"))
                .andExpect(jsonPath("$[0].isbn").value("123456789"))
                .andExpect(jsonPath("$[0].publicationYear").value("2010"))
                .andExpect(jsonPath("$[0].category.id").value(1))
                .andExpect(jsonPath("$[0].category.name").value("Art"))
                .andExpect(jsonPath("$[0].authors[0].id").value(1))
                .andExpect(jsonPath("$[0].authors[0].name").value("Messi"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Book2"))
                .andExpect(jsonPath("$[1].isbn").value("123456785"))
                .andExpect(jsonPath("$[1].publicationYear").value("2011"))
                .andExpect(jsonPath("$[1].category.id").value(1))
                .andExpect(jsonPath("$[1].category.name").value("Art"))
                .andExpect(jsonPath("$[1].authors[0].id").value(1))
                .andExpect(jsonPath("$[1].authors[0].name").value("Messi"));
    }

    @Test
    public void testThatFindBookByIdReturns200OkWhenBookExist() throws Exception {
        Category category = Category.builder().id(1L).name("Art").build();
        Author author = Author.builder().id(1L).name("Messi").build();
        Book book = Book.builder()
                .id(1L)
                .title("Book1")
                .isbn("123456789")
                .publicationYear("2010")
                .category(category)
                .authors(List.of(author))
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        var result = mockMvc.perform(get("/api/Books/1"));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Book1"))
                .andExpect(jsonPath("$.isbn").value("123456789"))
                .andExpect(jsonPath("$.publicationYear").value("2010"))
                .andExpect(jsonPath("$.category.id").value(1))
                .andExpect(jsonPath("$.category.name").value("Art"))
                .andExpect(jsonPath("$.authors[0].id").value(1))
                .andExpect(jsonPath("$.authors[0].name").value("Messi"));
    }

    @Test
    public void testThatFindBookByIdReturns404NotFoundWhenBookNotExist() throws Exception {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        var result = mockMvc.perform(get("/api/Books/1"));

        result.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatRemoveBookReturns200OkWhenBookExist() throws Exception {
        Category category = Category.builder().id(1L).name("Art").build();
        Author author = Author.builder().id(1L).name("Messi").build();
        Book book = Book.builder()
                .id(1L)
                .title("Book1")
                .isbn("123456789")
                .publicationYear("2010")
                .category(category)
                .authors(List.of(author))
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        var result = mockMvc.perform(delete("/api/Books/1"));

        result.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatRemoveBookReturns404NotFoundWhenBookNotExist() throws Exception {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        var result = mockMvc.perform(delete("/api/Books/999"));

        result.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatAddBookReturns201CreatedWhenSuccess() throws Exception {
        Category category = Category.builder().id(1L).name("Art").build();
        Author author = Author.builder().id(1L).name("Messi").build();
        AddBookDto book = AddBookDto.builder()
                .isbn("123456789")
                .title("Book Title")
                .publicationYear("2018")
                .categoryId(category.getId())
                .authorIds(List.of(author.getId()))
                .build();

        String bookJson = objectMapper.writeValueAsString(book);

        when(bookRepository.existsByTitle(book.getTitle())).thenReturn(false);
        when(bookRepository.existsByIsbn(book.getIsbn())).thenReturn(false);
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(authorRepository.findById(author.getId())).thenReturn(Optional.of(author));

        var result = mockMvc.perform(post("/api/Books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        );

        result.andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatAddBookReturns409ConflictWhenTitleAlreadyExists() throws Exception {
        Category category = Category.builder().id(1L).name("Art").build();
        Author author = Author.builder().id(1L).name("Messi").build();
        AddBookDto book = AddBookDto.builder()
                .isbn("123456789")
                .title("Book Title")
                .publicationYear("2018")
                .categoryId(category.getId())
                .authorIds(List.of(author.getId()))
                .build();

        String bookJson = objectMapper.writeValueAsString(book);

        when(bookRepository.existsByTitle(book.getTitle())).thenReturn(true);

        var result = mockMvc.perform(post("/api/Books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        );

        result.andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatAddBookReturns409ConflictWhenIsbnAlreadyExists() throws Exception {
        Category category = Category.builder().id(1L).name("Art").build();
        Author author = Author.builder().id(1L).name("Messi").build();
        AddBookDto book = AddBookDto.builder()
                .isbn("123456789")
                .title("Book Title")
                .publicationYear("2018")
                .categoryId(category.getId())
                .authorIds(List.of(author.getId()))
                .build();

        String bookJson = objectMapper.writeValueAsString(book);

        when(bookRepository.existsByTitle(book.getTitle())).thenReturn(false);
        when(bookRepository.existsByIsbn(book.getIsbn())).thenReturn(true);

        var result = mockMvc.perform(post("/api/Books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        );

        result.andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatAddBookReturns404NotFoundWhenCategoryNotFound() throws Exception {
        Category category = Category.builder().id(1L).name("Art").build();
        Author author = Author.builder().id(1L).name("Messi").build();
        AddBookDto book = AddBookDto.builder()
                .isbn("123456789")
                .title("Book Title")
                .publicationYear("2018")
                .categoryId(category.getId())
                .authorIds(List.of(author.getId()))
                .build();

        String bookJson = objectMapper.writeValueAsString(book);
        when(bookRepository.existsByTitle(book.getTitle())).thenReturn(false);
        when(bookRepository.existsByIsbn(book.getIsbn())).thenReturn(false);
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        var result = mockMvc.perform(post("/api/Books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        );

        result.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatAddBookReturns404NotFoundWhenAuthorNotFound() throws Exception {
        Category category = Category.builder().id(1L).name("Art").build();
        Author author = Author.builder().id(1L).name("Messi").build();
        AddBookDto book = AddBookDto.builder()
                .isbn("123456789")
                .title("Book Title")
                .publicationYear("2018")
                .categoryId(category.getId())
                .authorIds(List.of(author.getId()))
                .build();

        String bookJson = objectMapper.writeValueAsString(book);
        when(bookRepository.existsByTitle(book.getTitle())).thenReturn(false);
        when(bookRepository.existsByIsbn(book.getIsbn())).thenReturn(false);
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(authorRepository.findById(999L)).thenReturn(Optional.of(author));

        var result = mockMvc.perform(post("/api/Books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)
        );

        result.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatUpdateBookReturns200OkWhenBookExist() throws Exception {
        Category category = Category.builder().id(1L).name("Art").build();
        Author author = Author.builder().id(1L).name("Messi").build();
        Book book = Book.builder()
                .id(1L)
                .title("Book1")
                .isbn("123456789")
                .publicationYear("2010")
                .category(category)
                .authors(List.of(author))
                .build();

        AddBookDto request = AddBookDto.builder()
                .title("Updated Title")
                .isbn("123456789")
                .publicationYear("2011")
                .categoryId(1L)
                .authorIds(List.of(1L))
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        var result = mockMvc.perform(put("/api/Books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        result.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatUpdateBookReturns404NotFoundWhenBookNotExist() throws Exception {
        AddBookDto request = AddBookDto.builder()
                .title("Updated Title")
                .isbn("123456789")
                .publicationYear("2011")
                .categoryId(1L)
                .authorIds(List.of(1L))
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        var result = mockMvc.perform(put("/api/Books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        result.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatUpdateBookReturns404NotFoundWhenCategoryNotExist() throws Exception {
        Category category = Category.builder().id(1L).name("Art").build();
        Author author = Author.builder().id(1L).name("Messi").build();
        Book book = Book.builder()
                .id(1L)
                .title("Book1")
                .isbn("123456789")
                .publicationYear("2010")
                .category(category)
                .authors(List.of(author))
                .build();

        AddBookDto request = AddBookDto.builder()
                .title("Updated Title")
                .isbn("123456789")
                .publicationYear("2011")
                .categoryId(1L)
                .authorIds(List.of(1L))
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        var result = mockMvc.perform(put("/api/Books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        result.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatUpdateBookReturns404NotFoundWhenAuthorNotExist() throws Exception {
        Category category = Category.builder().id(1L).name("Art").build();
        Author author = Author.builder().id(1L).name("Messi").build();
        Book book = Book.builder()
                .id(1L)
                .title("Book1")
                .isbn("123456789")
                .publicationYear("2010")
                .category(category)
                .authors(List.of(author))
                .build();

        AddBookDto request = AddBookDto.builder()
                .title("Updated Title")
                .isbn("123456789")
                .publicationYear("2011")
                .categoryId(1L)
                .authorIds(List.of(1L))
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        var result = mockMvc.perform(put("/api/Books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        result.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatUpdateBookReturns409ConflictWhenIsbnExist() throws Exception {
        Category category = Category.builder().id(1L).name("Art").build();
        Author author = Author.builder().id(1L).name("Messi").build();
        Book book = Book.builder()
                .id(1L)
                .title("Book1")
                .isbn("123456789")
                .publicationYear("2010")
                .category(category)
                .authors(List.of(author))
                .build();

        AddBookDto request = AddBookDto.builder()
                .title("Updated Title")
                .isbn("123456789")
                .publicationYear("2011")
                .categoryId(1L)
                .authorIds(List.of(1L))
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.existsByIsbnAndIdNot("123456789", 1L)).thenReturn(true);

        var result = mockMvc.perform(put("/api/Books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        result.andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatUpdateBookReturns409ConflictWhenTitleExist() throws Exception {
        Category category = Category.builder().id(1L).name("Art").build();
        Author author = Author.builder().id(1L).name("Messi").build();
        Book book = Book.builder()
                .id(1L)
                .title("Book1")
                .isbn("123456789")
                .publicationYear("2010")
                .category(category)
                .authors(List.of(author))
                .build();

        AddBookDto request = AddBookDto.builder()
                .title("Updated Title")
                .isbn("123456789")
                .publicationYear("2011")
                .categoryId(1L)
                .authorIds(List.of(1L))
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.existsByTitleAndIdNot("Updated Title", 1L)).thenReturn(true);

        var result = mockMvc.perform(put("/api/Books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        result.andExpect(status().isConflict());
    }
}
