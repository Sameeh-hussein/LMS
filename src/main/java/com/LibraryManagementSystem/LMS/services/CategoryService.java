package com.LibraryManagementSystem.LMS.services;

import com.LibraryManagementSystem.LMS.dto.AddCategoryDto;
import com.LibraryManagementSystem.LMS.dto.ReturnCategoryDto;

import java.util.List;

public interface CategoryService {
    void addCategory(AddCategoryDto category);

    List<ReturnCategoryDto> findAllCategories();

    ReturnCategoryDto findCategoryById(Long categoryId);

    void removeCategory(Long categoryId);
}
