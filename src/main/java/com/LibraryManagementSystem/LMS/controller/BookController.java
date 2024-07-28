package com.LibraryManagementSystem.LMS.controller;

import com.LibraryManagementSystem.LMS.dto.AddBookDto;
import com.LibraryManagementSystem.LMS.dto.ReturnBookDto;
import com.LibraryManagementSystem.LMS.services.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Books")
@RequestMapping(value = "/api/Books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get all borrows")
    public ResponseEntity<List<ReturnBookDto>> getAllBooks() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookService.findAllBooks());
    }

    @GetMapping(value = "{bookId}")
    @Operation(summary = "Get a specific book by its ID")
    public ResponseEntity<ReturnBookDto> getBookById(@PathVariable Long bookId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookService.findById(bookId));
    }

    @PostMapping
    @Operation(summary = "Add a new book entry")
    public ResponseEntity<String> addBook(@Valid @RequestBody AddBookDto request) {
        bookService.addBook(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Book added successfully");
    }

    @PutMapping(value = "{bookId}")
    @Operation(summary = "Update a specific book by its ID")
    public ResponseEntity<String> updateBook(@PathVariable Long bookId, @Valid @RequestBody AddBookDto request) {
        bookService.updateBook(bookId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Book updated successfully");
    }

    @DeleteMapping(value = "{bookId}")
    @Operation(summary = "Delete a specific book by it ID")
    public ResponseEntity<String> deleteBook(@PathVariable Long bookId) {
        bookService.removeBook(bookId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Book deleted successfully");
    }

    @PostMapping("{bookId}/images")
    @Operation(summary = "Set the images of a specific book by it ID")
    public ResponseEntity<List<String>> addImage(@PathVariable Long bookId, @RequestParam("images") List<MultipartFile> files) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookService.updateBookImages(bookId, files));
    }
}
