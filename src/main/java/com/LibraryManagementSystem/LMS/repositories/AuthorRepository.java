package com.LibraryManagementSystem.LMS.repositories;

import com.LibraryManagementSystem.LMS.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    Boolean existsByName(String name);

    Boolean existsByNameAndIdNot(String name, Long id);
}
