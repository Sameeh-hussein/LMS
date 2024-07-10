package com.LibraryManagementSystem.LMS.controller;

import com.LibraryManagementSystem.LMS.dto.AddAuthorDto;
import com.LibraryManagementSystem.LMS.dto.ReturnAuthorDto;
import com.LibraryManagementSystem.LMS.services.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/authors")
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<List<ReturnAuthorDto>> getAllAuthors() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authorService.findAllAuthors());
    }

    @PostMapping
    public ResponseEntity<String> addAuthor(@Valid @RequestBody AddAuthorDto request) {
        authorService.addAuthor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Author added successfully");
    }
}
