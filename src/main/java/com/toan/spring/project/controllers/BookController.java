package com.toan.spring.project.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;

import com.toan.spring.project.common.BookStatus;
import com.toan.spring.project.models.Book;
import com.toan.spring.project.payload.response.MessageResponse;
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
        try {
            return ResponseEntity.ok(bookService.addNewBook(book));
        } catch (AccessDeniedException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Bạn không có quyền thêm sách"));
        } catch (Exception e) {
            return ResponseEntity.ok(new MessageResponse("Error: Có lỗi"));
        }
    }

    @PutMapping("/admin/updatebook")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateBook(@RequestBody Book book) {
        try {
            return ResponseEntity.ok(bookService.editBook(book));
        } catch (AccessDeniedException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Bạn không có quyền sửa sách"));
        } catch (Exception e) {
            return ResponseEntity.ok(new MessageResponse("Error: Có lỗi"));
        }

    }

    // xem lại
    @DeleteMapping("/admin/deletebook")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBook(@RequestBody Book book) {
        try {
            bookService.deleteBook(book);
            return ResponseEntity.ok("Successfully Deleted");
        } catch (AccessDeniedException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Bạn không có quyền xóa sách"));
        } catch (Exception e) {
            return ResponseEntity.ok(new MessageResponse("Error: Có lỗi"));
        }

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
