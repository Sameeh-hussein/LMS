package com.LibraryManagementSystem.LMS.domain;

import com.LibraryManagementSystem.LMS.enums.BorrowStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Borrows")
public class Borrow {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private Timestamp borrowDate;

    @Column(nullable = false)
    private Timestamp returnDate;

    @Enumerated(EnumType.STRING)
    private BorrowStatus status;
}
