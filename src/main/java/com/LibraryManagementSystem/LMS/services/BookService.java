package com.LibraryManagementSystem.LMS.services;

import com.LibraryManagementSystem.LMS.dto.AddBookDto;
import com.LibraryManagementSystem.LMS.dto.ReturnBookDto;

import java.util.List;

public interface BookService {
    List<ReturnBookDto> findAllBooks();

    void addBook(AddBookDto request);

    ReturnBookDto findById(Long bookId);

    void updateBook(Long bookId, AddBookDto request);
}
