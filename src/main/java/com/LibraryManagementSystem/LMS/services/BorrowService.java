package com.LibraryManagementSystem.LMS.services;

import com.LibraryManagementSystem.LMS.dto.AddBorrowDto;
import com.LibraryManagementSystem.LMS.dto.ReturnBorrowDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BorrowService {
    void addBorrow(AddBorrowDto request);

    Page<ReturnBorrowDto> findAllBorrows(Pageable pageable);

    ReturnBorrowDto findBorrowById(Long borrowId);

    void setBorrowStatusReturned(Long borrowId);

    void updateOverdueBorrows();

    void removeBorrow(Long borrowId);

    Page<ReturnBorrowDto> findBorrowsByUserId(Long userId, Pageable pageable);
}
