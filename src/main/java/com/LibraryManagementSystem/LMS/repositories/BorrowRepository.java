package com.LibraryManagementSystem.LMS.repositories;

import com.LibraryManagementSystem.LMS.domain.Borrow;
import com.LibraryManagementSystem.LMS.enums.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long>, PagingAndSortingRepository<Borrow, Long> {
    Boolean existsByBookIdAndUserId(Long bookId, Long userId);

    List<Borrow> findByStatusAndReturnDateBefore(BorrowStatus status, Timestamp returnDate);

    Page<Borrow> findByUserId(Long userId, Pageable pageable);
}
