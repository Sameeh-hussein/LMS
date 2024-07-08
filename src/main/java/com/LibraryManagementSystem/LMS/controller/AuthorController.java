package com.LibraryManagementSystem.LMS.controller;

import com.LibraryManagementSystem.LMS.dto.AddAuthorDto;
import com.LibraryManagementSystem.LMS.services.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/authors")
public class AuthorController {
    private final AuthorService authorService;

    @PostMapping
    public ResponseEntity<String> addAuthor(@Valid @RequestBody AddAuthorDto request) {
        authorService.addAuthor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Author added successfully");
    }
}
