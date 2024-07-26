package com.LibraryManagementSystem.LMS.services;

import com.LibraryManagementSystem.LMS.dto.AddBookDto;
import com.LibraryManagementSystem.LMS.dto.ReturnBookDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BookService {
    List<ReturnBookDto> findAllBooks();

    void addBook(AddBookDto request);

    ReturnBookDto findById(Long bookId);

    void updateBook(Long bookId, AddBookDto request);

    void removeBook(Long bookId);

    List<String> updateBookImages(Long bookId, List<MultipartFile> files);
}
