package com.LibraryManagementSystem.LMS.scheduling;

import com.LibraryManagementSystem.LMS.services.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BorrowScheduler {
    private final BorrowService borrowService;

    @Scheduled(fixedRate = 3600000) // Runs every hour
    public void updateOverdueBorrows() {
        borrowService.updateOverdueBorrows();
    }
}
