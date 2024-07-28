package com.LibraryManagementSystem.LMS.services.impl;

import com.LibraryManagementSystem.LMS.domain.Author;
import com.LibraryManagementSystem.LMS.dto.AddAuthorDto;
import com.LibraryManagementSystem.LMS.dto.ReturnAuthorDto;
import com.LibraryManagementSystem.LMS.exceptions.AuthorAlreadyExistException;
import com.LibraryManagementSystem.LMS.exceptions.AuthorNotFoundException;
import com.LibraryManagementSystem.LMS.mappers.impl.AuthorRequestMapper;
import com.LibraryManagementSystem.LMS.mappers.impl.AuthorReturnMapper;
import com.LibraryManagementSystem.LMS.repositories.AuthorRepository;
import com.LibraryManagementSystem.LMS.services.AuthorService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorRequestMapper authorRequestMapper;
    private final AuthorReturnMapper authorReturnMapper;

    @Override
    @CacheEvict(value = "authors", allEntries = true)
    public void addAuthor(@NonNull AddAuthorDto request) {
        if (authorRepository.existsByName(request.getName())) {
            throw new AuthorAlreadyExistException("Author with name " + request.getName() + " already exists");
        }

        authorRepository.save(authorRequestMapper.mapFrom(request));
    }

    @Override
    @Cacheable(value = "authors")
    public List<ReturnAuthorDto> findAllAuthors() {
        return authorRepository.findAll().stream()
                .map(authorReturnMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "author", key = "#authorId")
    public ReturnAuthorDto findAuthorById(Long authorId) {
        return authorRepository.findById(authorId)
                .map(authorReturnMapper::mapTo)
                .orElseThrow(() -> new AuthorNotFoundException("Author with id " + authorId + " not found"));
    }

    @Override
    @CacheEvict(value = {"author", "authors"}, allEntries = true)
    public void removeAuthor(Long authorId) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException("Author with id " + authorId + " not found"));

        author.getBooks().forEach(book -> book.getAuthors().remove(author));

        author.getBooks().clear();

        authorRepository.delete(author);
    }

    @Override
    @CachePut(value = "author", key = "#authorId")
    @CacheEvict(value = "authors", allEntries = true)
    public void updateAuthor(Long authorId, @NonNull AddAuthorDto request) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException("Author with id " + authorId + " not found"));

        if (authorRepository.existsByNameAndIdNot(request.getName(), authorId)) {
            throw new AuthorAlreadyExistException("Author with name " + request.getName() + " already exists");
        }

        author.setName(request.getName());

        authorRepository.save(author);
    }
}
