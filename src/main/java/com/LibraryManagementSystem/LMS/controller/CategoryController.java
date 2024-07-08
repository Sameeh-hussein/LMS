package com.LibraryManagementSystem.LMS.controller;

import com.LibraryManagementSystem.LMS.dto.AddCategoryDto;
import com.LibraryManagementSystem.LMS.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<String> addCategory(@Valid @RequestBody AddCategoryDto category) {
        categoryService.addCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Category added successfully");
    }
}
