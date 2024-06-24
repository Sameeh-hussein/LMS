package com.LibraryManagementSystem.LMS.mappers.impl;

import com.LibraryManagementSystem.LMS.domain.Book;
import com.LibraryManagementSystem.LMS.dto.ReturnBookDto;
import com.LibraryManagementSystem.LMS.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class BookReturnMapper implements Mapper<Book, ReturnBookDto> {

    private final ModelMapper modelMapper;

    public BookReturnMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ReturnBookDto mapTo(Book book) {
        return modelMapper.map(book, ReturnBookDto.class);
    }

    @Override
    public Book mapFrom(ReturnBookDto returnBookDto) {
        return modelMapper.map(returnBookDto, Book.class);
    }
}
