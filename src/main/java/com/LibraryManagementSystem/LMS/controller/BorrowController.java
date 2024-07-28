package com.LibraryManagementSystem.LMS.controller;

import com.LibraryManagementSystem.LMS.dto.AddBorrowDto;
import com.LibraryManagementSystem.LMS.dto.ReturnBorrowDto;
import com.LibraryManagementSystem.LMS.services.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Borrows")
@RequestMapping(value = "/api/borrows")
public class BorrowController {
    private final BorrowService borrowService;

    @GetMapping
    @Operation(summary = "Get all borrows with pagination")
    public ResponseEntity<Page<ReturnBorrowDto>> findAllBorrows(@ParameterObject Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(borrowService.findAllBorrows(pageable));
    }

    @GetMapping(value = "/user/{userId}")
    @Operation(summary = "Get all borrows by a specific user with pagination")
    public ResponseEntity<Page<ReturnBorrowDto>> findBorrowsByUserId(@PathVariable Long userId, @ParameterObject Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(borrowService.findBorrowsByUserId(userId, pageable));
    }

    @GetMapping(value = "{borrowId}")
    @Operation(summary = "Get a specific borrow by its ID")
    public ResponseEntity<ReturnBorrowDto> findBorrow(@PathVariable Long borrowId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(borrowService.findBorrowById(borrowId));
    }

    @PostMapping
    @Operation(summary = "Add a new borrow entry")
    public ResponseEntity<String> addBorrow(@Valid @RequestBody AddBorrowDto request) {
        borrowService.addBorrow(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Borrow added successfully");
    }

    @PatchMapping(value = "{borrowId}/returned")
    @Operation(summary = "Set the status of a specific borrow to 'Returned', for logged in user")
    public ResponseEntity<String> returnBook(@PathVariable Long borrowId) {
        borrowService.setBorrowStatusReturned(borrowId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Borrow approved successfully");
    }

    @DeleteMapping(value = "{borrowId}")
    @Operation(summary = "Delete a specific borrow by its ID")
    public ResponseEntity<String> deleteBorrow(@PathVariable Long borrowId) {
        borrowService.removeBorrow(borrowId);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Borrow deleted successfully");
    }
}
