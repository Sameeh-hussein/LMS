package com.LibraryManagementSystem.LMS.mappers.impl;

import com.LibraryManagementSystem.LMS.domain.Category;
import com.LibraryManagementSystem.LMS.dto.AddCategoryDto;
import com.LibraryManagementSystem.LMS.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryRequestMapper implements Mapper<Category, AddCategoryDto> {
    private final ModelMapper modelMapper;

    @Override
    public AddCategoryDto mapTo(Category category) {
        return modelMapper.map(category, AddCategoryDto.class);
    }

    @Override
    public Category mapFrom(AddCategoryDto addCategoryDto) {
        return modelMapper.map(addCategoryDto, Category.class);
    }
}
