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

    @GetMapping(value = "{categoryId}")
    public ResponseEntity<ReturnCategoryDto> getCategoryById(@PathVariable Long categoryId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(categoryService.findCategoryById(categoryId));
    }

    @PostMapping
    public ResponseEntity<String> addCategory(@Valid @RequestBody AddCategoryDto category) {
        categoryService.addCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Category added successfully");
    }

    @DeleteMapping(value = "{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
        categoryService.removeCategory(categoryId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Category deleted successfully");
    }
}
