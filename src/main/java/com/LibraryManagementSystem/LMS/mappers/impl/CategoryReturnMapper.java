package com.LibraryManagementSystem.LMS.mappers.impl;

import com.LibraryManagementSystem.LMS.domain.Category;
import com.LibraryManagementSystem.LMS.dto.ReturnCategoryDto;
import com.LibraryManagementSystem.LMS.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryReturnMapper implements Mapper<Category, ReturnCategoryDto> {
    private final ModelMapper modelMapper;

    @Override
    public ReturnCategoryDto mapTo(Category category) {
        return modelMapper.map(category, ReturnCategoryDto.class);
    }

    @Override
    public Category mapFrom(ReturnCategoryDto returnCategoryDto) {
        return modelMapper.map(returnCategoryDto, Category.class);
    }
}
