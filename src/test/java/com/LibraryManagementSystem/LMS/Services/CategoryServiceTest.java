package com.LibraryManagementSystem.LMS.Services;

import com.LibraryManagementSystem.LMS.domain.Category;
import com.LibraryManagementSystem.LMS.dto.AddCategoryDto;
import com.LibraryManagementSystem.LMS.dto.ReturnCategoryDto;
import com.LibraryManagementSystem.LMS.exceptions.CategoryAlreadyExistException;
import com.LibraryManagementSystem.LMS.exceptions.CategoryNotFoundException;
import com.LibraryManagementSystem.LMS.mappers.impl.CategoryRequestMapper;
import com.LibraryManagementSystem.LMS.mappers.impl.CategoryReturnMapper;
import com.LibraryManagementSystem.LMS.repositories.CategoryRepository;
import com.LibraryManagementSystem.LMS.services.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryReturnMapper categoryReturnMapper;

    @Mock
    private CategoryRequestMapper categoryRequestMapper;

    @InjectMocks
    private CategoryServiceImpl underTest;

    @Test
    public void testThatFindAllCategoriesReturnsAllCategories() {
        Category category1 = Category.builder().id(1L).name("Art").build();
        Category category2 = Category.builder().id(2L).name("Fashion").build();
        Category category3 = Category.builder().id(3L).name("Sport").build();

        ReturnCategoryDto categoryDto1 = ReturnCategoryDto.builder().id(1L).name("Art").build();
        ReturnCategoryDto categoryDto2 = ReturnCategoryDto.builder().id(2L).name("Fashion").build();
        ReturnCategoryDto categoryDto3 = ReturnCategoryDto.builder().id(3L).name("Sport").build();

        when(categoryRepository.findAll()).thenReturn(List.of(category1, category2, category3));
        when(categoryReturnMapper.mapTo(category1)).thenReturn(categoryDto1);
        when(categoryReturnMapper.mapTo(category2)).thenReturn(categoryDto2);
        when(categoryReturnMapper.mapTo(category3)).thenReturn(categoryDto3);

        var result = underTest.findAllCategories();

        assertEquals(3, result.size());
        assertEquals(categoryDto1, result.get(0));
        assertEquals(categoryDto2, result.get(1));
        assertEquals(categoryDto3, result.get(2));

        verify(categoryRepository, times(1)).findAll();
        verify(categoryReturnMapper, times(3)).mapTo(any());
    }

    @Test
    public void testThatFindCategoryByIdReturnsCategoryWhenExist() {
        Category category = Category.builder().id(1L).name("Art").build();
        ReturnCategoryDto categoryDto = ReturnCategoryDto.builder().id(1L).name("Art").build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryReturnMapper.mapTo(category)).thenReturn(categoryDto);

        var result = underTest.findCategoryById(1L);

        assertEquals(categoryDto, result);

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryReturnMapper, times(1)).mapTo(any());
    }

    @Test
    public void testThatFindCategoryByIdThrowCategoryNotFoundWhenNotExist() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> {
            underTest.findCategoryById(999L);
        });

        assertEquals("Category with id: 999 not found", exception.getMessage());

        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryReturnMapper, never()).mapTo(any());
    }

    @Test
    public void testThatAddCategorySaveCategoryWhenNameNotExist() {
        Category category = Category.builder().id(1L).name("Art").build();
        AddCategoryDto categoryDto = AddCategoryDto.builder().name("Art").build();

        when(categoryRepository.existsByName(categoryDto.getName())).thenReturn(false);
        when(categoryRequestMapper.mapFrom(categoryDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);

        underTest.addCategory(categoryDto);

        verify(categoryRepository, times(1)).existsByName(categoryDto.getName());
        verify(categoryRepository, times(1)).save(category);
        verify(categoryRequestMapper, times(1)).mapFrom(categoryDto);
    }

    @Test
    public void testThatAddCategoryThrowCategoryAlreadyExistExceptionWhenNameExist() {
        Category category = Category.builder().id(1L).name("Art").build();
        AddCategoryDto categoryDto = AddCategoryDto.builder().name("Art").build();

        when(categoryRepository.existsByName(categoryDto.getName())).thenReturn(true);

        CategoryAlreadyExistException exception = assertThrows(CategoryAlreadyExistException.class, () -> underTest.addCategory(categoryDto));

        assertEquals("Category with name: Art already exist", exception.getMessage());

        verify(categoryRepository, times(1)).existsByName(categoryDto.getName());
        verify(categoryRepository, never()).save(any(Category.class));
        verify(categoryRequestMapper, never()).mapFrom(any(AddCategoryDto.class));
    }

    @Test
    public void testThatRemoveCategoryDeleteCategoryWhenExist() {
        Category category = Category.builder().id(1L).name("Art").build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        underTest.removeCategory(1L);

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    public void testThatRemoveCategoryThrowCategoryNotFoundExceptionWhenNotExist() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> underTest.removeCategory(999L));

        assertEquals("Category with id: 999 not found", exception.getMessage());

        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
