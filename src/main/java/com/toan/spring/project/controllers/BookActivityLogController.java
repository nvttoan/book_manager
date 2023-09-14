package com.toan.spring.project.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toan.spring.project.models.BookActivityLog;
import com.toan.spring.project.repository.BookActivityLogRepository;
import com.toan.spring.project.services.BookActivityLogService;

@RestController
@RequestMapping("/api")
public class BookActivityLogController {
    @Autowired
    BookActivityLogService bookActivityLogService;

    @GetMapping("/listbooklog")
    public List<BookActivityLog> getAllLog() {
        return bookActivityLogService.getAllBookLog();
    }
}
