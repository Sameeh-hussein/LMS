package com.LibraryManagementSystem.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddBookDto {

    @NotBlank(message = "Book title is required")
    private String title;

    @NotBlank(message = "Book isbn is required")
    private String isbn;

    @NotBlank(message = "publication year is required")
    private String publicationYear;

    @NotNull(message = "Category Id is required")
    private Long categoryId;

    private List<Long> authorIds;
}
