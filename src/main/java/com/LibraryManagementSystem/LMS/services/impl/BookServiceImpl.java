package com.LibraryManagementSystem.LMS.services.impl;

import com.LibraryManagementSystem.LMS.domain.Author;
import com.LibraryManagementSystem.LMS.domain.Book;
import com.LibraryManagementSystem.LMS.domain.Category;
import com.LibraryManagementSystem.LMS.dto.AddBookDto;
import com.LibraryManagementSystem.LMS.dto.ReturnBookDto;
import com.LibraryManagementSystem.LMS.exceptions.AuthorNotFoundException;
import com.LibraryManagementSystem.LMS.exceptions.CategoryNotFoundException;
import com.LibraryManagementSystem.LMS.mappers.impl.BookRequestMapper;
import com.LibraryManagementSystem.LMS.mappers.impl.BookReturnMapper;
import com.LibraryManagementSystem.LMS.repositories.AuthorRepository;
import com.LibraryManagementSystem.LMS.repositories.BookRepository;
import com.LibraryManagementSystem.LMS.repositories.CategoryRepository;
import com.LibraryManagementSystem.LMS.services.BookService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookReturnMapper bookReturnMapper;
    private final BookRequestMapper bookRequestMapper;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;

    public BookServiceImpl(BookRepository bookRepository,
                           BookReturnMapper bookReturnMapper,
                           BookRequestMapper bookRequestMapper,
                           CategoryRepository categoryRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.bookReturnMapper = bookReturnMapper;
        this.bookRequestMapper = bookRequestMapper;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    public List<ReturnBookDto> findAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookReturnMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    public void addBook(AddBookDto request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category with id: " + request.getCategoryId() + " not found"));

        List<Author> authors = request.getAuthorIds().stream()
                .map(authorId -> {
                    return authorRepository.findById(authorId)
                            .orElseThrow(() -> new AuthorNotFoundException("Author with id: " + authorId + " not found"));
                })
                .toList();

        Book book = Book.builder()
                .title(request.getTitle())
                .isbn(request.getIsbn())
                .publicationYear(request.getPublicationYear())
                .category(category)
                .authors(authors)
                .build();

        bookRepository.save(book);
    }
}
