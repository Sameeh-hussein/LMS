package com.LibraryManagementSystem.LMS.repositories;

import com.LibraryManagementSystem.LMS.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
