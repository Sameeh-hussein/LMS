package com.LibraryManagementSystem.LMS.repositories;

import com.LibraryManagementSystem.LMS.domain.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {
}
