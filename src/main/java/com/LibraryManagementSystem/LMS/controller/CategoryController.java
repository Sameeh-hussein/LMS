package com.LibraryManagementSystem.LMS.controller;

import com.LibraryManagementSystem.LMS.dto.AddCategoryDto;
import com.LibraryManagementSystem.LMS.dto.ReturnCategoryDto;
import com.LibraryManagementSystem.LMS.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Categories")
@RequestMapping(value = "/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<List<ReturnCategoryDto>> getAllCategories() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoryService.findAllCategories());
    }

    @GetMapping(value = "{categoryId}")
    @Operation(summary = "Get a specific category by its ID")
    public ResponseEntity<ReturnCategoryDto> getCategoryById(@PathVariable Long categoryId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoryService.findCategoryById(categoryId));
    }

    @PostMapping
    @Operation(summary = "Add a new category entry")
    public ResponseEntity<String> addCategory(@Valid @RequestBody AddCategoryDto category) {
        categoryService.addCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Category added successfully");
    }

    @DeleteMapping(value = "{categoryId}")
    @Operation(summary = "Delete a specific category by its ID")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        categoryService.removeCategory(categoryId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Category deleted successfully");
    }
}
