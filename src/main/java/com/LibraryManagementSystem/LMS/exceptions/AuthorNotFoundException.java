package com.LibraryManagementSystem.LMS.exceptions;

public class AuthorNotFoundException extends RuntimeException{

    public AuthorNotFoundException(String message){
        super(message);
    }
}
