package com.LibraryManagementSystem.LMS.services.impl;

import com.LibraryManagementSystem.LMS.domain.Category;
import com.LibraryManagementSystem.LMS.dto.AddCategoryDto;
import com.LibraryManagementSystem.LMS.dto.ReturnCategoryDto;
import com.LibraryManagementSystem.LMS.exceptions.CategoryAlreadyExistException;
import com.LibraryManagementSystem.LMS.exceptions.CategoryNotFoundException;
import com.LibraryManagementSystem.LMS.mappers.impl.CategoryRequestMapper;
import com.LibraryManagementSystem.LMS.mappers.impl.CategoryReturnMapper;
import com.LibraryManagementSystem.LMS.repositories.CategoryRepository;
import com.LibraryManagementSystem.LMS.services.CategoryService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryRequestMapper categoryRequestMapper;
    private final CategoryReturnMapper categoryReturnMapper;

    @Override
    public void addCategory(@NotNull AddCategoryDto category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new CategoryAlreadyExistException("Category with name: " + category.getName() + " already exist");
        }
        categoryRepository.save(categoryRequestMapper.mapFrom(category));
    }

    @Override
    public List<ReturnCategoryDto> findAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryReturnMapper::mapTo)
                .collect(Collectors.toList());
    }

    @Override
    public ReturnCategoryDto findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(categoryReturnMapper::mapTo)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id: " + categoryId + " not found"));
    }

    @Override
    public void removeCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category with id: " + categoryId + " not found"));

        categoryRepository.delete(category);
    }
}
