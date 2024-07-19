package com.LibraryManagementSystem.LMS.exceptions;

public class BorrowNotFoundException extends RuntimeException{
    public BorrowNotFoundException(String message) {
        super(message);
    }
}
