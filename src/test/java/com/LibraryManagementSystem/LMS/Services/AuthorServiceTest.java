package com.LibraryManagementSystem.LMS.Services;

import com.LibraryManagementSystem.LMS.domain.Author;
import com.LibraryManagementSystem.LMS.dto.AddAuthorDto;
import com.LibraryManagementSystem.LMS.dto.ReturnAuthorDto;
import com.LibraryManagementSystem.LMS.exceptions.AuthorAlreadyExistException;
import com.LibraryManagementSystem.LMS.exceptions.AuthorNotFoundException;
import com.LibraryManagementSystem.LMS.mappers.impl.AuthorRequestMapper;
import com.LibraryManagementSystem.LMS.mappers.impl.AuthorReturnMapper;
import com.LibraryManagementSystem.LMS.repositories.AuthorRepository;
import com.LibraryManagementSystem.LMS.services.impl.AuthorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthorServiceTest {
    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorReturnMapper authorReturnMapper;

    @Mock
    private AuthorRequestMapper authorRequestMapper;

    @InjectMocks
    private AuthorServiceImpl underTest;

    @Test
    public void testThatFindAllAuthorsReturnsAllAuthorsSuccessfully() {
        Author author1 = Author.builder().id(1L).name("Messi").build();
        Author author2 = Author.builder().id(2L).name("Emiliano").build();
        Author author3 = Author.builder().id(3L).name("Di Maria").build();

        ReturnAuthorDto authorDto1 = ReturnAuthorDto.builder().id(1L).name("Messi").build();
        ReturnAuthorDto authorDto2 = ReturnAuthorDto.builder().id(2L).name("Emiliano").build();
        ReturnAuthorDto authorDto3 = ReturnAuthorDto.builder().id(3L).name("Di Maria").build();

        when(authorRepository.findAll()).thenReturn(List.of(author1, author2, author3));
        when(authorReturnMapper.mapTo(author1)).thenReturn(authorDto1);
        when(authorReturnMapper.mapTo(author2)).thenReturn(authorDto2);
        when(authorReturnMapper.mapTo(author3)).thenReturn(authorDto3);

        var result = underTest.findAllAuthors();

        assertEquals(3, result.size());
        assertEquals(authorDto1, result.get(0));
        assertEquals(authorDto2, result.get(1));
        assertEquals(authorDto3, result.get(2));

        verify(authorRepository, times(1)).findAll();
        verify(authorReturnMapper, times(3)).mapTo(any(Author.class));
    }

    @Test
    public void testThatFindAuthorByIdReturnsAuthorWhenExist() {
        Author author = Author.builder().id(1L).name("Messi").build();
        ReturnAuthorDto authorDto = ReturnAuthorDto.builder().id(1L).name("Messi").build();

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorReturnMapper.mapTo(author)).thenReturn(authorDto);

        var result = underTest.findAuthorById(1L);

        assertEquals(authorDto, result);
        verify(authorRepository, times(1)).findById(1L);
        verify(authorReturnMapper, times(1)).mapTo(author);
    }

    @Test
    public void testThatFindAuthorByIdThrowAuthorNotFoundExceptionWhenNotExist() {
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        AuthorNotFoundException exception = assertThrows(AuthorNotFoundException.class, () -> underTest.findAuthorById(999L));

        assertNotNull(exception);
        assertEquals("Author with id 999 not found", exception.getMessage());

        verify(authorRepository, times(1)).findById(999L);
        verify(authorReturnMapper, never()).mapTo(any(Author.class));
    }

    @Test
    public void testThatAddAuthorSaveAuthorWhenNameNotExist() {
        Author author = Author.builder().id(1L).name("Messi").build();
        AddAuthorDto authorDto = AddAuthorDto.builder().name("Messi").build();

        when(authorRepository.existsByName("Messi")).thenReturn(false);
        when(authorRepository.save(author)).thenReturn(author);
        when(authorRequestMapper.mapFrom(authorDto)).thenReturn(author);

        underTest.addAuthor(authorDto);

        verify(authorRepository, times(1)).save(author);
        verify(authorRequestMapper, times(1)).mapFrom(authorDto);
    }

    @Test
    public void testThatAddAuthorThrowAuthorAlreadyExistExceptionWhenNameExist() {
        AddAuthorDto authorDto = AddAuthorDto.builder().name("Messi").build();

        when(authorRepository.existsByName("Messi")).thenReturn(true);

        AuthorAlreadyExistException exception = assertThrows(AuthorAlreadyExistException.class, () -> underTest.addAuthor(authorDto));

        assertNotNull(exception);
        assertEquals("Author with name Messi already exists", exception.getMessage());

        verify(authorRepository, times(1)).existsByName("Messi");
    }

    @Test
    public void testThatUpdateAuthorSaveAuthorWhenAuthorExist() {
        Author author = Author.builder().id(1L).name("Messi").build();
        Author updatedAuthor = Author.builder().id(1L).name("Emiliano").build();

        AddAuthorDto authorDto = AddAuthorDto.builder().name("Emiliano").build();

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.existsByNameAndIdNot(updatedAuthor.getName(), updatedAuthor.getId())).thenReturn(false);

        underTest.updateAuthor(1L, authorDto);

        verify(authorRepository, times(1)).save(author);
        verify(authorRepository, times(1)).existsByNameAndIdNot(updatedAuthor.getName(), updatedAuthor.getId());
        verify(authorRepository, times(1)).findById(1L);
    }

    @Test
    public void testThatUpdateAuthorThrowAuthorNotFoundExceptionWhenAuthorNotExist() {
        Author updatedAuthor = Author.builder().id(1L).name("Emiliano").build();

        AddAuthorDto authorDto = AddAuthorDto.builder().name("Emiliano").build();

        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        AuthorNotFoundException exception = assertThrows(AuthorNotFoundException.class, () -> underTest.updateAuthor(999L, authorDto));

        assertNotNull(exception);
        assertEquals("Author with id 999 not found", exception.getMessage());

        verify(authorRepository, times(1)).findById(999L);
        verify(authorRepository, never()).existsByNameAndIdNot(updatedAuthor.getName(), updatedAuthor.getId());
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    public void testThatUpdateAuthorThrowAlreadyExistExceptionWhenAuthorNameExist() {
        Author author = Author.builder().id(1L).name("Messi").build();
        Author updatedAuthor = Author.builder().id(1L).name("Emiliano").build();

        AddAuthorDto authorDto = AddAuthorDto.builder().name("Emiliano").build();

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.existsByNameAndIdNot(updatedAuthor.getName(), updatedAuthor.getId())).thenReturn(true);

        AuthorAlreadyExistException exception = assertThrows(AuthorAlreadyExistException.class, () -> underTest.updateAuthor(1L, authorDto));

        assertNotNull(exception);
        assertEquals("Author with name Emiliano already exists", exception.getMessage());

        verify(authorRepository, times(1)).findById(1L);
        verify(authorRepository, times(1)).existsByNameAndIdNot(updatedAuthor.getName(), updatedAuthor.getId());
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    public void testThatRemoveAuthorDeleteAuthorWhenExist() {
        Author author = Author.builder().id(1L).name("Messi").books(new ArrayList<>()).build();

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        underTest.removeAuthor(1L);

        verify(authorRepository, times(1)).findById(1L);
        verify(authorRepository, times(1)).delete(author);
    }

    @Test
    public void testThatRemoveAuthorThrowAuthorNotFoundExceptionWhenNotExist() {
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        AuthorNotFoundException exception = assertThrows(AuthorNotFoundException.class, () -> underTest.removeAuthor(999L));

        assertNotNull(exception);
        assertEquals("Author with id 999 not found", exception.getMessage());

        verify(authorRepository, times(1)).findById(999L);
        verify(authorRepository, never()).delete(any(Author.class));
    }
}
