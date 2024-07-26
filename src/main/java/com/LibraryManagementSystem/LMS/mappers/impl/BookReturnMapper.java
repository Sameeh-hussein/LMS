package com.LibraryManagementSystem.LMS.mappers.impl;

import com.LibraryManagementSystem.LMS.domain.Book;
import com.LibraryManagementSystem.LMS.domain.BookImage;
import com.LibraryManagementSystem.LMS.dto.ReturnBookDto;
import com.LibraryManagementSystem.LMS.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookReturnMapper implements Mapper<Book, ReturnBookDto> {
    private final ModelMapper modelMapper;
    private final CategoryReturnMapper categoryReturnMapper;
    private final AuthorReturnMapper authorReturnMapper;

    @Override
    public ReturnBookDto mapTo(Book book) {
        return ReturnBookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .publicationYear(book.getPublicationYear())
                .category(categoryReturnMapper.mapTo(book.getCategory()))
                .authors(book.getAuthors().stream()
                        .map(authorReturnMapper::mapTo)
                        .collect(Collectors.toList()))
                .bookImages(book.getBookImages().stream()
                        .map(BookImage::getPath)
                        .collect(Collectors.toList()))
                .build();    }

    @Override
    public Book mapFrom(ReturnBookDto returnBookDto) {
        return modelMapper.map(returnBookDto, Book.class);
    }
}
