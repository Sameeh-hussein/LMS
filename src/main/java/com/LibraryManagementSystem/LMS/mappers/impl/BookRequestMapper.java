package com.LibraryManagementSystem.LMS.mappers.impl;

import com.LibraryManagementSystem.LMS.domain.Book;
import com.LibraryManagementSystem.LMS.dto.AddBookDto;
import com.LibraryManagementSystem.LMS.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookRequestMapper implements Mapper<Book, AddBookDto> {
    private final ModelMapper modelMapper;

    @Override
    public AddBookDto mapTo(Book book) {
        return modelMapper.map(book, AddBookDto.class);
    }

    @Override
    public Book mapFrom(AddBookDto addBookDto) {
        return modelMapper.map(addBookDto, Book.class);
    }
}
