package com.LibraryManagementSystem.LMS.controller;

import com.LibraryManagementSystem.LMS.dto.AddCategoryDto;
import com.LibraryManagementSystem.LMS.dto.ReturnCategoryDto;
import com.LibraryManagementSystem.LMS.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<ReturnCategoryDto>> getAllCategories() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoryService.findAllCategories());
    }

    @PostMapping
    public ResponseEntity<String> addCategory(@Valid @RequestBody AddCategoryDto category) {
        categoryService.addCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Category added successfully");
    }
}
