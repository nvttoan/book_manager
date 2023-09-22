package com.toan.spring.project.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import com.toan.spring.project.common.BookStatus;
import com.toan.spring.project.models.Book;
import com.toan.spring.project.payload.response.StringResponse;
import com.toan.spring.project.payload.response.ObjectResponse;
import com.toan.spring.project.repository.BookRepository;
import com.toan.spring.project.services.BookService;

import io.micrometer.common.util.StringUtils;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BookController {
    @Autowired
    private BookService bookService;
    @Autowired
    private BookRepository bookRepository;

    @RequestMapping(value = { "/allbooks" }, method = RequestMethod.GET)
    public ResponseEntity<Object> getAllBooks() {
        try {
            List<Book> books = bookService.getAllBooks();
            return ResponseEntity.ok(new ObjectResponse(0, books));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StringResponse(1, "Thực hiện thất bại: " + e.getMessage()));
        }
    }

    @PostMapping("/admin/addbook")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        try {
            if (StringUtils.isBlank(book.getTitle()) || StringUtils.isBlank(book.getAuthor())
                    || StringUtils.isBlank(book.getCode()) || (book.getStatus()) == null) {
                return ResponseEntity.badRequest().body(new StringResponse(1, "Thiếu thông tin sách"));
            }
            bookService.addNewBook(book);
            return ResponseEntity.ok(new StringResponse(0, "Thêm sách thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StringResponse(2, "Thực hiện thất bại: " + e.getMessage()));
        }
    }

    @PutMapping("/admin/updatebook")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateBook(@RequestBody Book book) {
        try {
            if (StringUtils.isBlank(book.getTitle()) || StringUtils.isBlank(book.getAuthor())
                    || StringUtils.isBlank(book.getCode()) || (book.getStatus()) == null) {
                return ResponseEntity.badRequest().body(new StringResponse(1, "Thiếu thông tin sách"));
            }
            bookService.editBook(book);
            return ResponseEntity.ok(new StringResponse(0, "Sửa sách thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StringResponse(2, "Thực hiện thất bại: " + e.getMessage()));
        }

    }

    @DeleteMapping("/admin/deletebook/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            if (!bookRepository.existsById(id)) {
                return ResponseEntity.badRequest().body(new StringResponse(1, "Sách không tồn tại"));
            }
            bookService.deleteBook(id);
            return ResponseEntity.badRequest().body(new StringResponse(0, "Xóa sách thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StringResponse(2, "Thực hiện thất bại: " + e.getMessage()));
        }

    }

    // find
    @GetMapping("/user/findbook")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> findBooks(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "author", required = false) String author,
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "status", required = false) BookStatus status) {

        try {
            List<Book> books = bookService.findBooks(title, author, code, status);

            if (books.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new StringResponse(1, "Không có sách nào được tìm thấy."));
            } else {
                return ResponseEntity.ok(new ObjectResponse(0, books));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StringResponse(2, "Thực hiện thất bại: " + e.getMessage()));
        }
    }

    // sắp xếp
    @GetMapping("/user/sortbook")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllBooksSortedByTitle() {
        try {
            List<Book> books = bookService.getAllBooksSortedByTitle();
            return ResponseEntity.ok(new ObjectResponse(0, books));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StringResponse(1, "Thực hiện thất bại: " + e.getMessage()));
        }
    }
}
