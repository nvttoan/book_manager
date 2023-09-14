package com.toan.spring.project.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.toan.spring.project.models.BookActivityLog;
import com.toan.spring.project.repository.BookActivityLogRepository;

@Service
public class BookActivityLogService {
    @Autowired
    BookActivityLogRepository bookActivityLogRepository;

    public List<BookActivityLog> getAllBookLog() {
        return bookActivityLogRepository.findAll();
    }
}
