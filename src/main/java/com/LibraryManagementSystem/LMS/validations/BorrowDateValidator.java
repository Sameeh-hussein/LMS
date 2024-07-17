package com.LibraryManagementSystem.LMS.validations;

import com.LibraryManagementSystem.LMS.dto.AddBorrowDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

public class BorrowDateValidator implements ConstraintValidator<ValidBorrowDates, AddBorrowDto> {
    private static final Logger logger = LoggerFactory.getLogger(BorrowDateValidator.class);

    @Override
    public boolean isValid(AddBorrowDto addBorrowDto, ConstraintValidatorContext context) {
        Timestamp borrowDate = addBorrowDto.getBorrowDate();
        Timestamp returnDate = addBorrowDto.getReturnDate();
        logger.debug("Validating borrow and return dates: borrowDate={}, returnDate={}", borrowDate, returnDate);

        return returnDate != null && borrowDate != null && returnDate.after(borrowDate);
    }
}