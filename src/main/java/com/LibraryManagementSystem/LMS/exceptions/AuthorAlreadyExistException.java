package com.LibraryManagementSystem.LMS.exceptions;

public class AuthorAlreadyExistException extends RuntimeException{

    public AuthorAlreadyExistException(String message){
        super(message);
    }
}
