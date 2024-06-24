package com.LibraryManagementSystem.LMS.services.impl;

import com.LibraryManagementSystem.LMS.dto.AddAuthorDto;
import com.LibraryManagementSystem.LMS.exceptions.AuthorAlreadyExistException;
import com.LibraryManagementSystem.LMS.mappers.impl.AuthorRequestMapper;
import com.LibraryManagementSystem.LMS.repositories.AuthorRepository;
import com.LibraryManagementSystem.LMS.services.AuthorService;
import org.springframework.stereotype.Service;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorRequestMapper authorRequestMapper;

    public AuthorServiceImpl(AuthorRepository authorRepository, AuthorRequestMapper authorRequestMapper) {
        this.authorRepository = authorRepository;
        this.authorRequestMapper = authorRequestMapper;
    }


    @Override
    public void addAuthor(AddAuthorDto request) {
        if (authorRepository.existsByName(request.getName())) {
            throw new AuthorAlreadyExistException("Author with name " + request.getName() + " already exists");
        }

        authorRepository.save(authorRequestMapper.mapFrom(request));
    }
}
