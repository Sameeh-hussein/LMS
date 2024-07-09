package com.LibraryManagementSystem.LMS.services.impl;

import com.LibraryManagementSystem.LMS.domain.Author;
import com.LibraryManagementSystem.LMS.domain.Book;
import com.LibraryManagementSystem.LMS.domain.Category;
import com.LibraryManagementSystem.LMS.dto.AddBookDto;
import com.LibraryManagementSystem.LMS.dto.ReturnBookDto;
import com.LibraryManagementSystem.LMS.exceptions.AuthorNotFoundException;
import com.LibraryManagementSystem.LMS.exceptions.BookAlreadyExistException;
import com.LibraryManagementSystem.LMS.exceptions.BookNotFoundException;
import com.LibraryManagementSystem.LMS.exceptions.CategoryNotFoundException;
import com.LibraryManagementSystem.LMS.mappers.impl.BookRequestMapper;
import com.LibraryManagementSystem.LMS.mappers.impl.BookReturnMapper;
import com.LibraryManagementSystem.LMS.repositories.AuthorRepository;
import com.LibraryManagementSystem.LMS.repositories.BookRepository;
import com.LibraryManagementSystem.LMS.repositories.CategoryRepository;
import com.LibraryManagementSystem.LMS.services.BookService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookReturnMapper bookReturnMapper;
    private final BookRequestMapper bookRequestMapper;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<ReturnBookDto> findAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookReturnMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    public ReturnBookDto findById(Long bookId) {
        return bookRepository.findById(bookId)
                .map(bookReturnMapper::mapTo)
                .orElseThrow(() -> new BookNotFoundException("Book with id: " + bookId + " not exist"));
    }

    @Override
    public void updateBook(Long bookId, @NotNull AddBookDto request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with id: " + bookId + " not exist"));

        if (bookRepository.existsByIsbnAndIdNot(request.getIsbn(), bookId)) {
            throw new BookAlreadyExistException("Book with isbn: " + request.getIsbn() + " already exist");
        }

        if (bookRepository.existsByTitleAndIdNot(request.getTitle(), bookId)) {
            throw new BookAlreadyExistException("Book with title: " + request.getTitle() + " already exist");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category with id: " + request.getCategoryId() + " not exist"));

        List<Author> authors = request.getAuthorIds().stream()
                .map(authorId -> authorRepository.findById(authorId)
                        .orElseThrow(() -> new AuthorNotFoundException("Author with id: " + authorId + " not exist")))
                .toList();

        modelMapper.map(request, book);
        book.setCategory(category);
        book.setAuthors(new ArrayList<>(authors));

        bookRepository.save(book);
    }

    @Override
    public void removeBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with id: " + bookId + " not exist"));

        bookRepository.delete(book);
    }

    @Override
    public void addBook(@NotNull AddBookDto request) {
        if (bookRepository.existsByTitle(request.getTitle())) {
            throw new BookAlreadyExistException("Book with title: " + request.getTitle() + " already exist");
        }

        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BookAlreadyExistException("Book with isbn: " + request.getIsbn() + " already exist");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category with id: " + request.getCategoryId() + " not found"));

        List<Author> authors = request.getAuthorIds().stream()
                .map(authorId -> authorRepository.findById(authorId)
                        .orElseThrow(() -> new AuthorNotFoundException("Author with id: " + authorId + " not found")))
                .toList();

        Book book = bookRequestMapper.mapFrom(request);
        book.setId(null);
        book.setCategory(category);
        book.setAuthors(authors);

        bookRepository.save(book);
    }
}
