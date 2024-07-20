package com.LibraryManagementSystem.LMS.services;

import com.LibraryManagementSystem.LMS.dto.AddBorrowDto;
import com.LibraryManagementSystem.LMS.dto.ReturnBorrowDto;

import java.util.List;

public interface BorrowService {
    void addBorrow(AddBorrowDto request);

    List<ReturnBorrowDto> findAllBorrows();

    ReturnBorrowDto findBorrowById(Long borrowId);

    void setBorrowStatusReturned(Long borrowId);

    void updateOverdueBorrows();

    void removeBorrow(Long borrowId);

    List<ReturnBorrowDto> findBorrowsByUserId(Long userId);
}
