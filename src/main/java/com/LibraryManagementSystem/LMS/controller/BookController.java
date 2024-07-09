package com.LibraryManagementSystem.LMS.controller;

import com.LibraryManagementSystem.LMS.dto.AddBookDto;
import com.LibraryManagementSystem.LMS.dto.ReturnBookDto;
import com.LibraryManagementSystem.LMS.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/Books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<ReturnBookDto>> getAllBooks() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookService.findAllBooks());
    }

    @GetMapping(value = "{bookId}")
    public ResponseEntity<ReturnBookDto> getBookById(@PathVariable Long bookId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(bookService.findById(bookId));
    }

    @PostMapping
    public ResponseEntity<String> addBook(@Valid @RequestBody AddBookDto request) {
        bookService.addBook(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Book added successfully");
    }

    @PutMapping(value = "{bookId}")
    public ResponseEntity<String> updateBook(@PathVariable Long bookId, @Valid @RequestBody AddBookDto request) {
        bookService.updateBook(bookId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Book updated successfully");
    }

    @DeleteMapping(value = "{bookId}")
    public ResponseEntity<String> deleteBook(@PathVariable Long bookId) {
        bookService.removeBook(bookId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Book deleted successfully");
    }
}
