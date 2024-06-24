package com.LibraryManagementSystem.LMS.services;

import com.LibraryManagementSystem.LMS.dto.AddAuthorDto;
import org.springframework.stereotype.Service;

public interface AuthorService {
    void addAuthor(AddAuthorDto request);
}
