package com.LibraryManagementSystem.LMS.controller;

import com.LibraryManagementSystem.LMS.dto.AddBorrowDto;
import com.LibraryManagementSystem.LMS.services.BorrowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
