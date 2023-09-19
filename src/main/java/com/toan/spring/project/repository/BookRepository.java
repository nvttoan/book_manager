package com.toan.spring.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toan.spring.project.common.BookStatus;
import com.toan.spring.project.models.Book;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsById(Long id);

    public Optional<Book> findByIdAndStatus(Long id, BookStatus status);

    public List<Book> findByTitleContainingOrAuthorContainingOrCodeOrStatus(String title, String author, String code,
            BookStatus status);

    List<Book> findAllByOrderByTitleAsc();
}