package com.LibraryManagementSystem.LMS.exceptions;

public class BorrowAlreadyExistException extends RuntimeException{
    public BorrowAlreadyExistException(String message) {
        super(message);
    }
}
