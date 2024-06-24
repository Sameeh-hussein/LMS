package com.LibraryManagementSystem.LMS.mappers.impl;

import com.LibraryManagementSystem.LMS.domain.Author;
import com.LibraryManagementSystem.LMS.dto.AddAuthorDto;
import com.LibraryManagementSystem.LMS.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AuthorRequestMapper implements Mapper<Author, AddAuthorDto> {
    private final ModelMapper modelMapper;
    public AuthorRequestMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public AddAuthorDto mapTo(Author author) {
        return modelMapper.map(author, AddAuthorDto.class);
    }

    @Override
    public Author mapFrom(AddAuthorDto addAuthorDto) {
        return modelMapper.map(addAuthorDto, Author.class);
    }
}
