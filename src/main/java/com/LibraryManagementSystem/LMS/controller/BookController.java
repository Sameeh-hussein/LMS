package com.LibraryManagementSystem.LMS.controller;

import com.LibraryManagementSystem.LMS.dto.AddBookDto;
import com.LibraryManagementSystem.LMS.dto.ReturnBookDto;
import com.LibraryManagementSystem.LMS.services.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/Books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

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
}
