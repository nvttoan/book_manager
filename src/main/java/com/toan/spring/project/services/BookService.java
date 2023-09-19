package com.toan.spring.project.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.toan.spring.project.common.BookStatus;
import com.toan.spring.project.models.Book;

@Service
public interface BookService {
    public List<Book> getAllBooks();

    public Book save(Book book);

    public Book findByIdAndStatus(Long id, BookStatus status);

    public void deleteBook(Long id);

    public Book addNewBook(Book book);

    public Book editBook(Book book);

    public Book findById(Long id);

    public List<Book> findBooks(String title, String author, String code, BookStatus status);

    public List<Book> getAllBooksSortedByTitle();

}
