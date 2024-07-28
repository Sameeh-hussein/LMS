package com.LibraryManagementSystem.LMS.controller;

import com.LibraryManagementSystem.LMS.dto.AddAuthorDto;
import com.LibraryManagementSystem.LMS.dto.ReturnAuthorDto;
import com.LibraryManagementSystem.LMS.services.AuthorService;
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
@Tag(name = "Authors")
@RequestMapping(value = "/api/authors")
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping
    @Operation(summary = "Get all authors")
    public ResponseEntity<List<ReturnAuthorDto>> getAllAuthors() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authorService.findAllAuthors());
    }

    @GetMapping(value = "{authorId}")
    @Operation(summary = "Get a specific author by its ID")
    public ResponseEntity<ReturnAuthorDto> getAuthorById(@PathVariable Long authorId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authorService.findAuthorById(authorId));
    }

    @PostMapping
    @Operation(summary = "Add a new author entry")
    public ResponseEntity<String> addAuthor(@Valid @RequestBody AddAuthorDto request) {
        authorService.addAuthor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Author added successfully");
    }

    @DeleteMapping(value = "{authorId}")
    @Operation(summary = "Delete a specific author by its ID")
    public ResponseEntity<String> deleteAuthor(@PathVariable Long authorId) {
        authorService.removeAuthor(authorId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Author deleted successfully");
    }

    @PutMapping(value = "{authorId}")
    @Operation(summary = "Update a specific author by its ID")
    public ResponseEntity<String> updateAuthor(@PathVariable Long authorId, @Valid @RequestBody AddAuthorDto request) {
        authorService.updateAuthor(authorId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Author updated successfully");
    }
}
