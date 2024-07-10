package com.LibraryManagementSystem.LMS.services.impl;

import com.LibraryManagementSystem.LMS.dto.AddAuthorDto;
import com.LibraryManagementSystem.LMS.dto.ReturnAuthorDto;
import com.LibraryManagementSystem.LMS.exceptions.AuthorAlreadyExistException;
import com.LibraryManagementSystem.LMS.mappers.impl.AuthorRequestMapper;
import com.LibraryManagementSystem.LMS.mappers.impl.AuthorReturnMapper;
import com.LibraryManagementSystem.LMS.repositories.AuthorRepository;
import com.LibraryManagementSystem.LMS.services.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorRequestMapper authorRequestMapper;
    private final AuthorReturnMapper authorReturnMapper;

    @Override
    public void addAuthor(@NotNull AddAuthorDto request) {
        if (authorRepository.existsByName(request.getName())) {
            throw new AuthorAlreadyExistException("Author with name " + request.getName() + " already exists");
        }

        authorRepository.save(authorRequestMapper.mapFrom(request));
    }

    @Override
    public List<ReturnAuthorDto> findAllAuthors() {
        return authorRepository.findAll().stream()
                .map(authorReturnMapper::mapTo)
                .collect(Collectors.toList());
    }
}
