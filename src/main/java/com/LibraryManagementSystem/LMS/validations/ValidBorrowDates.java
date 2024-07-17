package com.LibraryManagementSystem.LMS.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BorrowDateValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBorrowDates {
    String message() default "Return date must be after borrow date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}