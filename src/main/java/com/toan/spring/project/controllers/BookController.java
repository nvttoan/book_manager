package com.toan.spring.project.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import com.toan.spring.project.common.BookStatus;
import com.toan.spring.project.models.Book;
import com.toan.spring.project.services.BookService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BookController {
    @Autowired
    private BookService bookService;

    @RequestMapping(value = { "/allbooks" }, method = RequestMethod.GET)
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @PostMapping("/admin/addbook")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        return ResponseEntity.ok(bookService.addNewBook(book));
    }

    @PutMapping("/admin/updatebook")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateBook(@RequestBody Book book) {
        return ResponseEntity.ok(bookService.editBook(book));
    }

    // xem lại
    @DeleteMapping("/admin/deletebook")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBook(@RequestBody Book book) {
        bookService.deleteBook(book);
        return ResponseEntity.ok("Successfully Deleted");
    }

    // find
    @GetMapping("/user/findbook")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Book> findBooks(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "author", required = false) String author,
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "status", required = false) BookStatus status) {

        List<Book> books = bookService.findBooks(title, author, code, status);

        return books;
    }

    // sắp xếp
    @GetMapping("/user/sort")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Book> getAllBooksSortedByTitle() {
        return bookService.getAllBooksSortedByTitle();
    }
}
