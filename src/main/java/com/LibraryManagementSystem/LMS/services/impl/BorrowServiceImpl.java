package com.LibraryManagementSystem.LMS.services.impl;

import com.LibraryManagementSystem.LMS.domain.Book;
import com.LibraryManagementSystem.LMS.domain.Borrow;
import com.LibraryManagementSystem.LMS.domain.User;
import com.LibraryManagementSystem.LMS.dto.AddBorrowDto;
import com.LibraryManagementSystem.LMS.dto.ReturnBorrowDto;
import com.LibraryManagementSystem.LMS.enums.BorrowStatus;
import com.LibraryManagementSystem.LMS.exceptions.*;
import com.LibraryManagementSystem.LMS.mappers.impl.BookReturnMapper;
import com.LibraryManagementSystem.LMS.mappers.impl.UserReturnMapper;
import com.LibraryManagementSystem.LMS.repositories.BookRepository;
import com.LibraryManagementSystem.LMS.repositories.BorrowRepository;
import com.LibraryManagementSystem.LMS.repositories.UserRepository;
import com.LibraryManagementSystem.LMS.services.BorrowService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {
    private final BorrowRepository borrowRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookReturnMapper bookReturnMapper;
    private final UserReturnMapper userReturnMapper;

    @Override
    public void addBorrow(@NotNull AddBorrowDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User with Iid: " + request.getUserId() + " not found"));

        if (user.getRole().getName() != null && !user.getRole().getName().equals("MEMBER")) {
            throw new UserNotAuthorizedException("User with id: " + user.getId() + " is not authorized to add Borrow, must has member role");
        }

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book with id: " + request.getBookId() + " not found"));

        if (borrowRepository.existsByBookIdAndUserId(book.getId(), user.getId())) {
            throw new BorrowAlreadyExistException("User with id: " + user.getId() + " already borrowed the book with id: " + book.getId());
        }

        Borrow borrow = Borrow.builder()
                .user(user)
                .book(book)
                .status(BorrowStatus.BORROWED)
                .borrowDate(request.getBorrowDate())
                .returnDate(request.getReturnDate())
                .build();

        borrowRepository.save(borrow);
    }

    @Override
    public Page<ReturnBorrowDto> findAllBorrows(Pageable pageable) {
        return borrowRepository.findAll(pageable)
                .map(borrow -> ReturnBorrowDto.builder()
                        .id(borrow.getId())
                        .user(userReturnMapper.mapTo(borrow.getUser()))
                        .book(bookReturnMapper.mapTo(borrow.getBook()))
                        .borrowDate(borrow.getBorrowDate())
                        .returnDate(borrow.getReturnDate())
                        .status(borrow.getStatus())
                        .build()
                );
    }

    @Override
    public ReturnBorrowDto findBorrowById(Long borrowId) {
        return borrowRepository.findById(borrowId)
                .map(borrow -> ReturnBorrowDto.builder()
                        .id(borrow.getId())
                        .user(userReturnMapper.mapTo(borrow.getUser()))
                        .book(bookReturnMapper.mapTo(borrow.getBook()))
                        .borrowDate(borrow.getBorrowDate())
                        .returnDate(borrow.getReturnDate())
                        .status(borrow.getStatus())
                        .build()
                )
                .orElseThrow(() -> new BorrowNotFoundException("Borrow with id: " + borrowId + " not exists"));
    }

    @Override
    public void setBorrowStatusReturned(Long borrowId) {
        String authenticatedEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));

        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new BorrowNotFoundException("Borrow with id: " + borrowId + " not exists"));

        if (!currentUser.equals(borrow.getUser())) {
            throw new AccessDeniedException("You are not authorized to return this book");
        }

        if(borrow.getStatus().equals(BorrowStatus.RETURNED)) {
            throw new AlreadyReturnedException("You are already returned the book");
        }

        borrow.setReturnDate(new Timestamp(System.currentTimeMillis()));
        borrow.setStatus(BorrowStatus.RETURNED);

        borrowRepository.save(borrow);
    }

    @Override
    public void updateOverdueBorrows() {
        List<Borrow> overdueBorrows = borrowRepository.findByStatusAndReturnDateBefore(
                BorrowStatus.BORROWED, new Timestamp(System.currentTimeMillis())
        );
        overdueBorrows.forEach(borrow -> borrow.setStatus(BorrowStatus.OVERDUE));
        borrowRepository.saveAll(overdueBorrows);
    }

    @Override
    public void removeBorrow(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new BorrowNotFoundException("Borrow with id: " + borrowId + " not found"));

        borrowRepository.delete(borrow);
    }

    @Override
    public Page<ReturnBorrowDto> findBorrowsByUserId(Long userId, Pageable pageable) {
        return borrowRepository.findByUserId(userId, pageable)
                .map(borrow -> ReturnBorrowDto.builder()
                        .id(borrow.getId())
                        .user(userReturnMapper.mapTo(borrow.getUser()))
                        .book(bookReturnMapper.mapTo(borrow.getBook()))
                        .borrowDate(borrow.getBorrowDate())
                        .returnDate(borrow.getReturnDate())
                        .status(borrow.getStatus())
                        .build()
                );
    }
}
