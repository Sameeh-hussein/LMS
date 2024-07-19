package com.LibraryManagementSystem.LMS.Services;

import com.LibraryManagementSystem.LMS.domain.Author;
import com.LibraryManagementSystem.LMS.domain.Book;
import com.LibraryManagementSystem.LMS.domain.Category;
import com.LibraryManagementSystem.LMS.dto.AddBookDto;
import com.LibraryManagementSystem.LMS.dto.ReturnAuthorDto;
import com.LibraryManagementSystem.LMS.dto.ReturnBookDto;
import com.LibraryManagementSystem.LMS.dto.ReturnCategoryDto;
import com.LibraryManagementSystem.LMS.exceptions.AuthorNotFoundException;
import com.LibraryManagementSystem.LMS.exceptions.BookAlreadyExistException;
import com.LibraryManagementSystem.LMS.exceptions.BookNotFoundException;
import com.LibraryManagementSystem.LMS.exceptions.CategoryNotFoundException;
import com.LibraryManagementSystem.LMS.mappers.impl.BookRequestMapper;
import com.LibraryManagementSystem.LMS.mappers.impl.BookReturnMapper;
import com.LibraryManagementSystem.LMS.repositories.AuthorRepository;
import com.LibraryManagementSystem.LMS.repositories.BookRepository;
import com.LibraryManagementSystem.LMS.repositories.CategoryRepository;
import com.LibraryManagementSystem.LMS.services.impl.BookServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookReturnMapper bookReturnMapper;

    @Mock
    private BookRequestMapper bookRequestMapper;

    @InjectMocks
    private BookServiceImpl underTest;

    @Test
    public void testThatFindAllBooksReturnAllBooks() {
        Author author = Author.builder().id(1L).name("Messi").build();
        Category category = Category.builder().id(1L).name("History").build();
        Book book1 = Book.builder().id(1L).title("Book1").isbn("123").publicationYear("2009").category(category).authors(List.of(author)).build();
        Book book2 = Book.builder().id(2L).title("Book2").isbn("134").publicationYear("2010").category(category).authors(List.of(author)).build();

        ReturnAuthorDto authorDto = ReturnAuthorDto.builder().id(1L).name("Messi").build();
        ReturnCategoryDto categoryDto = ReturnCategoryDto.builder().id(1L).name("History").build();
        ReturnBookDto bookDto1 = ReturnBookDto.builder().id(1L).title("Book1").isbn("123").publicationYear("2009").category(categoryDto).authors(List.of(authorDto)).build();
        ReturnBookDto bookDto2 = ReturnBookDto.builder().id(2L).title("Book2").isbn("134").publicationYear("2010").category(categoryDto).authors(List.of(authorDto)).build();

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));
        when(bookReturnMapper.mapTo(book1)).thenReturn(bookDto1);
        when(bookReturnMapper.mapTo(book2)).thenReturn(bookDto2);

        var result = underTest.findAllBooks();

        assertEquals(2, result.size());
        assertEquals(bookDto1, result.get(0));
        assertEquals(bookDto2, result.get(1));

        verify(bookRepository, times(1)).findAll();
        verify(bookReturnMapper, times(2)).mapTo(any(Book.class));
    }

    @Test
    public void testThatFindBookByIdReturnBookWhenExist() {
        Author author = Author.builder().id(1L).name("Messi").build();
        Category category = Category.builder().id(1L).name("History").build();
        Book book = Book.builder().id(1L).title("Book1").isbn("123").publicationYear("2009").category(category).authors(List.of(author)).build();

        ReturnAuthorDto authorDto = ReturnAuthorDto.builder().id(1L).name("Messi").build();
        ReturnCategoryDto categoryDto = ReturnCategoryDto.builder().id(1L).name("History").build();
        ReturnBookDto bookDto = ReturnBookDto.builder().id(1L).title("Book1").isbn("123").publicationYear("2009").category(categoryDto).authors(List.of(authorDto)).build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookReturnMapper.mapTo(book)).thenReturn(bookDto);

        var result = underTest.findById(1L);

        assertEquals(bookDto, result);

        verify(bookRepository, times(1)).findById(1L);
        verify(bookReturnMapper, times(1)).mapTo(book);
    }

    @Test
    public void testThatFindBookByIdThrowBookNotFoundExceptionWhenNotExist() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> underTest.findById(999L));

        assertNotNull(exception);
        assertEquals("Book with id: 999 not exist", exception.getMessage());

        verify(bookRepository, times(1)).findById(999L);
        verify(bookReturnMapper, never()).mapTo(any(Book.class));
    }

    @Test
    public void testThatAddBookSaveBookWhenSuccess() {
        Author author = Author.builder().id(1L).name("Messi").build();
        Category category = Category.builder().id(1L).name("History").build();
        Book book = Book.builder().title("Book1").isbn("123").publicationYear("2009").category(category).authors(List.of(author)).build();

        AddBookDto bookDto = AddBookDto.builder().title("Book1").isbn("123").publicationYear("2009").categoryId(1L).authorIds(List.of(1L)).build();

        when(bookRepository.existsByTitle("Book1")).thenReturn(false);
        when(bookRepository.existsByIsbn("123")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        underTest.addBook(bookDto);

        verify(bookRepository, times(1)).existsByTitle("Book1");
        verify(bookRepository, times(1)).existsByIsbn("123");
        verify(categoryRepository, times(1)).findById(1L);
        verify(authorRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    public void testThatAddBookThrowBookAlreadyExistExceptionWhenTitleExist() {
        Author author = Author.builder().id(1L).name("Messi").build();
        Category category = Category.builder().id(1L).name("History").build();
        Book book = Book.builder().title("Book1").isbn("123").publicationYear("2009").category(category).authors(List.of(author)).build();

        AddBookDto bookDto = AddBookDto.builder().title("Book1").isbn("123").publicationYear("2009").categoryId(1L).authorIds(List.of(1L)).build();

        when(bookRepository.existsByTitle("Book1")).thenReturn(true);

        BookAlreadyExistException exception = assertThrows(BookAlreadyExistException.class, () -> underTest.addBook(bookDto));

        assertNotNull(exception);
        assertEquals("Book with title: Book1 already exist", exception.getMessage());

        verify(bookRepository, times(1)).existsByTitle("Book1");
        verify(bookRepository, never()).existsByIsbn("123");
        verify(categoryRepository, never()).findById(1L);
        verify(authorRepository, never()).findById(1L);
        verify(bookRepository, never()).save(book);
    }

    @Test
    public void testThatAddBookThrowBookAlreadyExistExceptionWhenIsbnExist() {
        Author author = Author.builder().id(1L).name("Messi").build();
        Category category = Category.builder().id(1L).name("History").build();
        Book book = Book.builder().title("Book1").isbn("123").publicationYear("2009").category(category).authors(List.of(author)).build();

        AddBookDto bookDto = AddBookDto.builder().title("Book1").isbn("123").publicationYear("2009").categoryId(1L).authorIds(List.of(1L)).build();

        when(bookRepository.existsByTitle("Book1")).thenReturn(false);
        when(bookRepository.existsByIsbn("123")).thenReturn(true);

        BookAlreadyExistException exception = assertThrows(BookAlreadyExistException.class, () -> underTest.addBook(bookDto));

        assertNotNull(exception);
        assertEquals("Book with isbn: 123 already exist", exception.getMessage());

        verify(bookRepository, times(1)).existsByTitle("Book1");
        verify(bookRepository, times(1)).existsByIsbn("123");
        verify(categoryRepository, never()).findById(1L);
        verify(authorRepository, never()).findById(1L);
        verify(bookRepository, never()).save(book);
    }

    @Test
    public void testThatAddBookThrowCategoryNotFoundExceptionWhenCategoryNotExist() {
        Author author = Author.builder().id(1L).name("Messi").build();
        Category category = Category.builder().id(1L).name("History").build();
        Book book = Book.builder().title("Book1").isbn("123").publicationYear("2009").category(category).authors(List.of(author)).build();

        AddBookDto bookDto = AddBookDto.builder().title("Book1").isbn("123").publicationYear("2009").categoryId(1L).authorIds(List.of(1L)).build();

        when(bookRepository.existsByTitle("Book1")).thenReturn(false);
        when(bookRepository.existsByIsbn("123")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> underTest.addBook(bookDto));

        assertNotNull(exception);
        assertEquals("Category with id: 1 not found", exception.getMessage());

        verify(bookRepository, times(1)).existsByTitle("Book1");
        verify(bookRepository, times(1)).existsByIsbn("123");
        verify(categoryRepository, times(1)).findById(1L);
        verify(authorRepository, never()).findById(1L);
        verify(bookRepository, never()).save(book);
    }

    @Test
    public void testThatAddBookThrowAuthorNotFoundExceptionWhenAuthorNotExist() {
        Author author = Author.builder().id(1L).name("Messi").build();
        Category category = Category.builder().id(1L).name("History").build();
        Book book = Book.builder().title("Book1").isbn("123").publicationYear("2009").category(category).authors(List.of(author)).build();

        AddBookDto bookDto = AddBookDto.builder().title("Book1").isbn("123").publicationYear("2009").categoryId(1L).authorIds(List.of(1L)).build();

        when(bookRepository.existsByTitle("Book1")).thenReturn(false);
        when(bookRepository.existsByIsbn("123")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        AuthorNotFoundException exception = assertThrows(AuthorNotFoundException.class, () -> underTest.addBook(bookDto));

        assertNotNull(exception);
        assertEquals("Author with id: 1 not found", exception.getMessage());

        verify(bookRepository, times(1)).existsByTitle("Book1");
        verify(bookRepository, times(1)).existsByIsbn("123");
        verify(categoryRepository, times(1)).findById(1L);
        verify(authorRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(book);
    }

    @Test
    public void testThatUpdateBookUpdatesBookWhenSuccess() {
        Author author = Author.builder().id(1L).name("Messi").build();
        Category category = Category.builder().id(1L).name("History").build();
        Book existingBook = Book.builder().id(1L).title("OldTitle").isbn("456").publicationYear("2005").category(category).authors(List.of(author)).build();

        AddBookDto updateDto = AddBookDto.builder().title("NewTitle").isbn("123").publicationYear("2009").categoryId(1L).authorIds(List.of(1L)).build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.existsByIsbnAndIdNot("123", 1L)).thenReturn(false);
        when(bookRepository.existsByTitleAndIdNot("NewTitle", 1L)).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        underTest.updateBook(1L, updateDto);

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).existsByIsbnAndIdNot("123", 1L);
        verify(bookRepository, times(1)).existsByTitleAndIdNot("NewTitle", 1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(authorRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(existingBook);

        assertEquals("NewTitle", existingBook.getTitle());
        assertEquals("123", existingBook.getIsbn());
        assertEquals("2009", existingBook.getPublicationYear());
    }

    @Test
    public void testThatUpdateBookThrowsBookAlreadyExistExceptionWhenIsbnExist() {
        AddBookDto updateDto = AddBookDto.builder().title("NewTitle").isbn("123").publicationYear("2009").categoryId(1L).authorIds(List.of(1L)).build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(new Book()));
        when(bookRepository.existsByIsbnAndIdNot("123", 1L)).thenReturn(true);

        BookAlreadyExistException exception = assertThrows(BookAlreadyExistException.class, () -> underTest.updateBook(1L, updateDto));

        assertNotNull(exception);
        assertEquals("Book with isbn: 123 already exist", exception.getMessage());

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).existsByIsbnAndIdNot("123", 1L);
        verify(bookRepository, never()).existsByTitleAndIdNot(anyString(), anyLong());
        verify(categoryRepository, never()).findById(anyLong());
        verify(authorRepository, never()).findById(anyLong());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void testThatUpdateBookThrowsBookAlreadyExistExceptionWhenTitleExist() {
        AddBookDto updateDto = AddBookDto.builder().title("NewTitle").isbn("123").publicationYear("2009").categoryId(1L).authorIds(List.of(1L)).build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(new Book()));
        when(bookRepository.existsByIsbnAndIdNot("123", 1L)).thenReturn(false);
        when(bookRepository.existsByTitleAndIdNot("NewTitle", 1L)).thenReturn(true);

        BookAlreadyExistException exception = assertThrows(BookAlreadyExistException.class, () -> underTest.updateBook(1L, updateDto));

        assertNotNull(exception);
        assertEquals("Book with title: NewTitle already exist", exception.getMessage());

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).existsByTitleAndIdNot("NewTitle", 1L);
        verify(bookRepository, times(1)).existsByIsbnAndIdNot("123", 1L);
        verify(categoryRepository, never()).findById(anyLong());
        verify(authorRepository, never()).findById(anyLong());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void testThatUpdateBookThrowsCategoryNotFoundExceptionWhenCategoryNotExist() {
        AddBookDto updateDto = AddBookDto.builder().title("NewTitle").isbn("123").publicationYear("2009").categoryId(1L).authorIds(List.of(1L)).build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(new Book()));
        when(bookRepository.existsByIsbnAndIdNot("123", 1L)).thenReturn(false);
        when(bookRepository.existsByTitleAndIdNot("NewTitle", 1L)).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> underTest.updateBook(1L, updateDto));

        assertNotNull(exception);
        assertEquals("Category with id: 1 not exist", exception.getMessage());

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).existsByIsbnAndIdNot("123", 1L);
        verify(bookRepository, times(1)).existsByTitleAndIdNot("NewTitle", 1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(authorRepository, never()).findById(anyLong());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void testThatUpdateBookThrowsAuthorNotFoundExceptionWhenAuthorNotExist() {
        AddBookDto updateDto = AddBookDto.builder().title("NewTitle").isbn("123").publicationYear("2009").categoryId(1L).authorIds(List.of(1L)).build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(new Book()));
        when(bookRepository.existsByIsbnAndIdNot("123", 1L)).thenReturn(false);
        when(bookRepository.existsByTitleAndIdNot("NewTitle", 1L)).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        AuthorNotFoundException exception = assertThrows(AuthorNotFoundException.class, () -> underTest.updateBook(1L, updateDto));

        assertNotNull(exception);
        assertEquals("Author with id: 1 not exist", exception.getMessage());

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).existsByIsbnAndIdNot("123", 1L);
        verify(bookRepository, times(1)).existsByTitleAndIdNot("NewTitle", 1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(authorRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    public void testThatRemoveBookRemovesBookWhenSuccess() {
        Book book = Book.builder().id(1L).title("Book1").isbn("123").publicationYear("2009").build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        underTest.removeBook(1L);

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    public void testThatRemoveBookThrowsBookNotFoundExceptionWhenBookNotExist() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> underTest.removeBook(1L));

        assertNotNull(exception);
        assertEquals("Book with id: 1 not exist", exception.getMessage());

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, never()).delete(any(Book.class));
    }

}
