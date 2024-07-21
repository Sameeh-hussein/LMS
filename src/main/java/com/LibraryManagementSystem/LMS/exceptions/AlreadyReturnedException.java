package com.LibraryManagementSystem.LMS.exceptions;

public class AlreadyReturnedException extends RuntimeException{
    public AlreadyReturnedException(String message){
        super(message);
    }
}
