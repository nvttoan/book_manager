package com.toan.spring.project.aspects;

import java.time.LocalDateTime;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.toan.spring.project.models.Book;
import com.toan.spring.project.models.BookActivityLog;
import com.toan.spring.project.models.User;
import com.toan.spring.project.repository.BookActivityLogRepository;

@Aspect
@Component
public class BookActivityLoggingAspect {
    private final BookActivityLogRepository bookActivityLogRepository;

    @Autowired
    public BookActivityLoggingAspect(BookActivityLogRepository bookActivityLogRepository) {
        this.bookActivityLogRepository = bookActivityLogRepository;
    }

    // @Before("execution(*
    // com.toan.spring.project.services.BookService.addNewBook(..)) && args(book)")
    // public void logBookAdded(Book book) {
    // logActivity("Create", book.getId());
    // }

    @Before("execution(* com.toan.spring.project.services.BookService.editBook(..)) && args(book)")
    public void logBookEdited(Book book) {
        logActivity("Update", book.getId());
    }

    @Before("execution(* com.toan.spring.project.services.BookService.deleteBook(..)) && args(book)")
    public void logBookDeleted(Book book) {
        logActivity("Delete", book.getId());
    }

    @Before("execution(* com.toan.spring.project.services.UserService.changeUserRoleToBanned(..)) && args(userId)")
    public void logUserBanned(long userId) {
        logActivity("BANNED_USER", userId);
    }

    public void logActivity(String activity, Long bookId) {
        BookActivityLog activityLog = new BookActivityLog();
        activityLog.setActivity(activity);
        activityLog.setBookUserId(bookId);
        activityLog.setTimestamp(LocalDateTime.now());
        bookActivityLogRepository.save(activityLog);
    }
}
