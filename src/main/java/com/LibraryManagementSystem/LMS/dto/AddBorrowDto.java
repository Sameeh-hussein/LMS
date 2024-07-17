package com.LibraryManagementSystem.LMS.dto;

import com.LibraryManagementSystem.LMS.validations.ValidBorrowDates;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidBorrowDates
public class AddBorrowDto {
    @NotNull(message = "User id is required")
    private Long userId;

    @NotNull(message = "Book id is required")
    private Long bookId;

    @NotNull(message = "Borrow date is required")
    private Timestamp borrowDate;

    @NotNull(message = "Return date is required")
    private Timestamp returnDate;
}
