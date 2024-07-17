package com.LibraryManagementSystem.LMS.validations;

import com.LibraryManagementSystem.LMS.dto.AddBorrowDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.sql.Timestamp;

public class BorrowDateValidator implements ConstraintValidator<ValidBorrowDates, AddBorrowDto> {

    @Override
    public boolean isValid(AddBorrowDto addBorrowDto, ConstraintValidatorContext context) {
        Timestamp borrowDate = addBorrowDto.getBorrowDate();
        Timestamp returnDate = addBorrowDto.getReturnDate();
        return returnDate != null && borrowDate != null && returnDate.after(borrowDate);
    }
}