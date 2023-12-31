package com.toan.spring.project.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.toan.spring.project.models.BookActivityLog;
import com.toan.spring.project.payload.response.StringResponse;
import com.toan.spring.project.payload.response.ObjectResponse;
import com.toan.spring.project.services.BookActivityLogService;

@RestController
@RequestMapping("/api")
public class BookActivityLogController {
    @Autowired
    BookActivityLogService bookActivityLogService;

    @GetMapping("/listbooklog")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getAllLog() {
        try {
            List<BookActivityLog> logs = bookActivityLogService.getAllBookLog();
            return ResponseEntity.ok(new ObjectResponse(0, logs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StringResponse(1, "Thực hiện thất bại: " + e.getMessage()));
        }
    }
}
