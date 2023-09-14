package com.toan.spring.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.toan.spring.project.models.BookActivityLog;

public interface BookActivityLogRepository extends JpaRepository<BookActivityLog, Long> {

}
