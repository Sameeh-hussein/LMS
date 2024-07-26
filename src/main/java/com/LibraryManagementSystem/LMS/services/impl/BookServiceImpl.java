package com.LibraryManagementSystem.LMS.services.impl;

import com.LibraryManagementSystem.LMS.domain.Author;
import com.LibraryManagementSystem.LMS.domain.Book;
import com.LibraryManagementSystem.LMS.domain.BookImage;
import com.LibraryManagementSystem.LMS.domain.Category;
import com.LibraryManagementSystem.LMS.dto.AddBookDto;
import com.LibraryManagementSystem.LMS.dto.ReturnBookDto;
import com.LibraryManagementSystem.LMS.exceptions.AuthorNotFoundException;
import com.LibraryManagementSystem.LMS.exceptions.BookAlreadyExistException;
import com.LibraryManagementSystem.LMS.exceptions.BookNotFoundException;
import com.LibraryManagementSystem.LMS.exceptions.CategoryNotFoundException;
import com.LibraryManagementSystem.LMS.mappers.impl.BookReturnMapper;
import com.LibraryManagementSystem.LMS.repositories.AuthorRepository;
import com.LibraryManagementSystem.LMS.repositories.BookImageRepository;
import com.LibraryManagementSystem.LMS.repositories.BookRepository;
import com.LibraryManagementSystem.LMS.repositories.CategoryRepository;
import com.LibraryManagementSystem.LMS.services.BookService;
import com.LibraryManagementSystem.LMS.services.FileStorageService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList("image/png", "image/jpeg", "image/jpg");

    private final BookRepository bookRepository;
    private final BookReturnMapper bookReturnMapper;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final FileStorageService fileStorageService;
    private final BookImageRepository bookImageRepository;

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

        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setPublicationYear(request.getPublicationYear());
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
    @Transactional
    public List<String> updateBookImages(Long bookId, List<MultipartFile> files) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with id: " + bookId + " not exist"));

        List<String> paths = new ArrayList<>();
        List<BookImage> images = files.stream()
                .map(file -> processFile(file, book, paths))
                .collect(Collectors.toList());

        book.setBookImages(images);

        bookRepository.save(book);
        return paths;
    }

    private BookImage processFile(MultipartFile file, Book book, List<String> paths) {
        if (file.getContentType() == null || !ALLOWED_FILE_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Invalid file type. Only PNG, JPG, and JPEG files are allowed.");
        }

        String path = null;
        try {
            path = fileStorageService.uploadImageToFileSystem(file, "book");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        paths.add(path);
        BookImage image = BookImage.builder()
                .name(file.getOriginalFilename())
                .path(path)
                .book(book)
                .uploadDate(LocalDateTime.now())
                .build();

        bookImageRepository.save(image);

        return image;
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
