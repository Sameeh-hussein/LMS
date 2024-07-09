package com.LibraryManagementSystem.LMS.services.impl;

import com.LibraryManagementSystem.LMS.dto.AddCategoryDto;
import com.LibraryManagementSystem.LMS.exceptions.CategoryAlreadyExistException;
import com.LibraryManagementSystem.LMS.mappers.impl.CategoryRequestMapper;
import com.LibraryManagementSystem.LMS.repositories.CategoryRepository;
import com.LibraryManagementSystem.LMS.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryRequestMapper categoryRequestMapper;

    @Override
    public void addCategory(@NotNull AddCategoryDto category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new CategoryAlreadyExistException("Category with name: " + category.getName() + " already exist");
        }
        categoryRepository.save(categoryRequestMapper.mapFrom(category));
    }
}
