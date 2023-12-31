package com.toan.spring.project.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import com.toan.spring.project.aspects.BookActivityLoggingAspect;
import com.toan.spring.project.common.BookStatus;
import com.toan.spring.project.exception.ResourceNotFoundException;
import com.toan.spring.project.models.Book;
import com.toan.spring.project.repository.BookRepository;
import com.toan.spring.project.services.BookService;

@Service
@EnableCaching

public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookActivityLoggingAspect bookActivityLog;

    @Override
    @Cacheable("books")
    public List<Book> getAllBooks() {
        doLongRunningTask();
        return bookRepository.findAll();
    }

    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book findByIdAndStatus(Long id, BookStatus status) {
        return bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unavailable book"));
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("book not delete with id:" + id));
        bookRepository.delete(book);
    }

    @Override
    public Book addNewBook(Book book) {
        Book savedBook = save(book); // Lưu sách vào cơ sở dữ liệu
        bookActivityLog.logActivity("Create", savedBook.getId()); // Sử dụng ID của sách sau khi đã lưu
        return savedBook;
    }

    @Override
    @CacheEvict(value = "book", key = "#book.id")
    public Book editBook(Book book) {
        if (!bookRepository.existsById(book.getId())) {
            throw new ResourceNotFoundException("Unavailable Book");
        }
        return save(book);
    }

    @Override
    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
    }

    @Override
    @Cacheable("books")
    public List<Book> findBooks(String title, String author, String code, BookStatus status) {
        return bookRepository.findByTitleContainingOrAuthorContainingOrCodeOrStatus(title, author, code, status);
    }

    // a-z
    @Override
    @Cacheable("books")
    public List<Book> getAllBooksSortedByTitle() {
        return bookRepository.findAllByOrderByTitleAsc();
    }

    private void doLongRunningTask() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
