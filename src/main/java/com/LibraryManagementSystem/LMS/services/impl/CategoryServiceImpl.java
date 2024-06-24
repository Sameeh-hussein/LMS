package com.LibraryManagementSystem.LMS.services.impl;

import com.LibraryManagementSystem.LMS.dto.AddCategoryDto;
import com.LibraryManagementSystem.LMS.mappers.impl.CategoryRequestMapper;
import com.LibraryManagementSystem.LMS.repositories.CategoryRepository;
import com.LibraryManagementSystem.LMS.services.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryRequestMapper categoryRequestMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryRequestMapper categoryRequestMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryRequestMapper = categoryRequestMapper;
    }


    @Override
    public void addCategory(AddCategoryDto category) {
        categoryRepository.save(categoryRequestMapper.mapFrom(category));
    }
}
