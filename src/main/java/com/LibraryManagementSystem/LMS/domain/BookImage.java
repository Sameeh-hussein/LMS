package com.LibraryManagementSystem.LMS.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "BookImages")
public class BookImage {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String path;
    private LocalDateTime uploadDate;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}
