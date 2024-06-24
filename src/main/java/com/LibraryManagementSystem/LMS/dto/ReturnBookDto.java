package com.LibraryManagementSystem.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnBookDto {

    private Long id;

    private String title;

    private String isbn;

    private String publicationYear;

    private ReturnCategoryDto category;

    private List<ReturnAuthorDto> authors;
}
