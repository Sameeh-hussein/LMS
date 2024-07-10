package com.LibraryManagementSystem.LMS.mappers.impl;

import com.LibraryManagementSystem.LMS.domain.Author;
import com.LibraryManagementSystem.LMS.dto.ReturnAuthorDto;
import com.LibraryManagementSystem.LMS.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorReturnMapper implements Mapper<Author, ReturnAuthorDto> {
    private final ModelMapper modelMapper;

    @Override
    public ReturnAuthorDto mapTo(Author author) {
        return modelMapper.map(author, ReturnAuthorDto.class);
    }

    @Override
    public Author mapFrom(ReturnAuthorDto returnAuthorDto) {
        return modelMapper.map(returnAuthorDto, Author.class);
    }
}
