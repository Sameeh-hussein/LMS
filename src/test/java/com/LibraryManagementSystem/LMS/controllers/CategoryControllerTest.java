package com.LibraryManagementSystem.LMS.controllers;

import com.LibraryManagementSystem.LMS.domain.Category;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class CategoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryRepository categoryRepository;

    @Test
    public void testThatGetCategoryByIdReturns200OkWhenCategoryExist() throws Exception {
        Category category = Category.builder()
                .id(1L)
                .name("Art")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        var result = mockMvc.perform(get("/api/categories/1"));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Art"));
    }

    @Test
    public void testThatGetCategoryByIdReturns404NotFoundWhenCategoryNotExist() throws Exception {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());
        var result = mockMvc.perform(get("/api/categories/999"));
        result.andExpect(status().isNotFound());
    }

    @Test
    public void testThatGetAllCategoriesReturns200Ok() throws Exception {
        Category category1 = Category.builder().id(1L).name("Art").build();
        Category category2 = Category.builder().id(2L).name("Technology").build();
        Category category3 = Category.builder().id(3L).name("History").build();

        when(categoryRepository.findAll()).thenReturn(List.of(category1, category2, category3));

        var result = mockMvc.perform(get("/api/categories"));

        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Art"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Technology"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("History"));
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatAddCategoryReturns201CreatedWhenSuccess() throws Exception {
        Category category = Category.builder().id(1L).name("Art").build();

        when(categoryRepository.save(category)).thenReturn(category);

        String categoryJson = objectMapper.writeValueAsString(category);

        var result = mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryJson)
        );

        result.andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatAddCategoryReturns409ConflictWhenTitleExist() throws Exception {
        Category category = Category.builder().id(1L).name("Art").build();

        when(categoryRepository.existsByName("Art")).thenReturn(true);

        String categoryJson = objectMapper.writeValueAsString(category);

        var result = mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryJson)
        );

        result.andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatRemoveCategoryReturns200OkWhenCategoryExist() throws Exception {
        Category category = Category.builder().id(1L).name("Art").build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        var result = mockMvc.perform(delete("/api/categories/1"));

        result.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "librarian", roles = {"LIBRARIAN"})
    public void testThatRemoveCategoryReturns404NotFoundWhenCategoryNotExist() throws Exception {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        var result = mockMvc.perform(delete("/api/categories/999"));

        result.andExpect(status().isNotFound());
    }
}
