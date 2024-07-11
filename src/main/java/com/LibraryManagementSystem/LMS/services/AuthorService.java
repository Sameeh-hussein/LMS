package com.LibraryManagementSystem.LMS.services;

import com.LibraryManagementSystem.LMS.dto.AddAuthorDto;
import com.LibraryManagementSystem.LMS.dto.ReturnAuthorDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface AuthorService {
    void addAuthor(AddAuthorDto request);

    List<ReturnAuthorDto> findAllAuthors();

    ReturnAuthorDto findAuthorById(Long authorId);

    void removeAuthor(Long authorId);

    void updateAuthor(Long authorId, AddAuthorDto request);
}
