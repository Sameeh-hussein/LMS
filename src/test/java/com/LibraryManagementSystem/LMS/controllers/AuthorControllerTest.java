package com.LibraryManagementSystem.LMS.controllers;

import com.LibraryManagementSystem.LMS.domain.Author;
import com.LibraryManagementSystem.LMS.exceptions.AuthorAlreadyExistException;
import com.LibraryManagementSystem.LMS.repositories.AuthorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class AuthorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthorRepository authorRepository;

    @Test
    public void testThatGetAllAuthorsReturns200Ok() throws Exception {
        Author author1 = Author.builder().id(1L).name("Sami").build();
        Author author2 = Author.builder().id(2L).name("Ahmad").build();
        Author author3 = Author.builder().id(3L).name("Messi").build();

        when(authorRepository.findAll()).thenReturn(List.of(author1, author2, author3));

        var result = mockMvc.perform(get("/api/authors"));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Sami"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Ahmad"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("Messi"));
    }

    @Test
    public void testThatGetAuthorByIdReturns200OkWhenAuthorExists() throws Exception {
        Author author = Author.builder().id(1L).name("Sami").build();

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.existsByName("Sami")).thenReturn(true);

        var result = mockMvc.perform(get("/api/authors/" + author.getId()));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Sami"));
    }

    @Test
    public void testThatGetAuthorByIdReturns404NotFoundWhenAuthorNotExists() throws Exception {
        Author author = Author.builder().id(1L).name("Sami").build();

        when(authorRepository.findById(1L)).thenReturn(Optional.empty());
        when(authorRepository.existsByName("Sami")).thenReturn(false);

        var result = mockMvc.perform(get("/api/authors/" + author.getId()));

        result.andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatAddAuthorReturns201CreatedWhenSuccessful() throws Exception {
        Author author = Author.builder().id(1L).name("Sami").build();
        String authorJson = objectMapper.writeValueAsString(author);

        when(authorRepository.save(author)).thenReturn(author);

        var result = mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorJson)
        );

        result.andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatAddAuthorReturns409ConflictWhenAuthorAlreadyExists() throws Exception {
        Author author = Author.builder().id(1L).name("Sami").build();
        String authorJson = objectMapper.writeValueAsString(author);

        when(authorRepository.existsByName("Sami")).thenReturn(true);

        var result = mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorJson)
        );

        result.andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatUpdateAuthorReturns200OkWhenAuthorExistsAndNameUnique() throws Exception {
        Author author = Author.builder().id(1L).name("Sami").build();
        Author updatedAuthor = Author.builder().id(1L).name("Ahmad").build();
        String authorUpdatedJson = objectMapper.writeValueAsString(updatedAuthor);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.existsByName("Ahmad")).thenReturn(false);
        when(authorRepository.save(author)).thenReturn(updatedAuthor);

        var result = mockMvc.perform(put("/api/authors/" + author.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorUpdatedJson)
        );

        result.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatUpdateAuthorReturns409ConflictWhenAuthorExistsAndNameNotUnique() throws Exception {
        Author author = Author.builder().id(1L).name("Sami").build();
        Author updatedAuthor = Author.builder().id(1L).name("Ahmad").build();
        String authorUpdatedJson = objectMapper.writeValueAsString(updatedAuthor);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.existsByName("Ahmad")).thenReturn(true);

        var result = mockMvc.perform(put("/api/authors/" + author.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorUpdatedJson)
        );

        result.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatUpdateAuthorReturns404NotFoundWhenAuthorNotExists() throws Exception {
        Author author = Author.builder().id(1L).name("Sami").build();
        Author updatedAuthor = Author.builder().id(1L).name("Ahmad").build();
        String authorUpdatedJson = objectMapper.writeValueAsString(updatedAuthor);

        when(authorRepository.findById(1L)).thenReturn(Optional.empty());
        when(authorRepository.existsByName("Ahmad")).thenReturn(true);

        var result = mockMvc.perform(put("/api/authors/" + author.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(authorUpdatedJson)
        );

        result.andExpect(status().isNotFound());
    }
}
