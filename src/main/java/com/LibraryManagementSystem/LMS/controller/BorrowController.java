package com.LibraryManagementSystem.LMS.controller;

import com.LibraryManagementSystem.LMS.dto.AddBorrowDto;
import com.LibraryManagementSystem.LMS.dto.ReturnBorrowDto;
import com.LibraryManagementSystem.LMS.services.BorrowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/borrows")
public class BorrowController {
    private final BorrowService borrowService;

    @PostMapping
    public ResponseEntity<String> addBorrow(@Valid @RequestBody AddBorrowDto request) {
        borrowService.addBorrow(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Borrow added successfully");
    }

    @GetMapping
    public ResponseEntity<Page<ReturnBorrowDto>> findAllBorrows(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(borrowService.findAllBorrows(pageable));
    }

    @GetMapping(value = "/user/{userId}")
    public ResponseEntity<Page<ReturnBorrowDto>> findBorrowsByUserId(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(borrowService.findBorrowsByUserId(userId, pageable));
    }

    @GetMapping(value = "{borrowId}")
    public ResponseEntity<ReturnBorrowDto> findBorrow(@PathVariable Long borrowId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(borrowService.findBorrowById(borrowId));
    }

    @PatchMapping(value = "{borrowId}/returned")
    public ResponseEntity<String> returnBook(@PathVariable Long borrowId) {
        borrowService.setBorrowStatusReturned(borrowId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Borrow approved successfully");
    }

    @DeleteMapping(value = "{borrowId}")
    public ResponseEntity<String> deleteBorrow(@PathVariable Long borrowId) {
        borrowService.removeBorrow(borrowId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Borrow deleted successfully");
    }
}
