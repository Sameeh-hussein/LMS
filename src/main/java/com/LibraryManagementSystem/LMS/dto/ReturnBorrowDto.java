package com.LibraryManagementSystem.LMS.dto;

import com.LibraryManagementSystem.LMS.enums.BorrowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnBorrowDto {
    private Long id;

    private ReturnUserDto user;

    private ReturnBookDto book;

    private Timestamp borrowDate;

    private Timestamp returnDate;

    private BorrowStatus status;
}
