package com.LibraryManagementSystem.LMS.services.impl;

import com.LibraryManagementSystem.LMS.domain.Book;
import com.LibraryManagementSystem.LMS.domain.Borrow;
import com.LibraryManagementSystem.LMS.domain.User;
import com.LibraryManagementSystem.LMS.dto.AddBorrowDto;
import com.LibraryManagementSystem.LMS.dto.ReturnBorrowDto;
import com.LibraryManagementSystem.LMS.enums.BorrowStatus;
import com.LibraryManagementSystem.LMS.exceptions.BookNotFoundException;
import com.LibraryManagementSystem.LMS.exceptions.BorrowAlreadyExistException;
import com.LibraryManagementSystem.LMS.exceptions.BorrowNotFoundException;
import com.LibraryManagementSystem.LMS.exceptions.UserNotFoundException;
import com.LibraryManagementSystem.LMS.mappers.impl.BookReturnMapper;
import com.LibraryManagementSystem.LMS.mappers.impl.UserReturnMapper;
import com.LibraryManagementSystem.LMS.repositories.BookRepository;
import com.LibraryManagementSystem.LMS.repositories.BorrowRepository;
import com.LibraryManagementSystem.LMS.repositories.UserRepository;
import com.LibraryManagementSystem.LMS.services.BorrowService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book with id: " + request.getBookId() + " not found"));

        if (borrowRepository.existsByBookIdAndUserId(book.getId(), user.getId())) {
            throw new BorrowAlreadyExistException("User with id: " + user.getId() + " already borrowed the book with id: " + book.getId());
        }

        Borrow borrow = Borrow.builder()
                .user(user)
                .book(book)
                .status(BorrowStatus.PENDING)
                .borrowDate(request.getBorrowDate())
                .returnDate(request.getReturnDate())
                .build();

        borrowRepository.save(borrow);
    }

    @Override
    public List<ReturnBorrowDto> findAllBorrows() {
        return borrowRepository.findAll().stream()
                .map(borrow -> ReturnBorrowDto.builder()
                        .id(borrow.getId())
                        .user(userReturnMapper.mapTo(borrow.getUser()))
                        .book(bookReturnMapper.mapTo(borrow.getBook()))
                        .borrowDate(borrow.getBorrowDate())
                        .returnDate(borrow.getReturnDate())
                        .status(borrow.getStatus())
                        .build()
                )
                .collect(Collectors.toList());
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
}