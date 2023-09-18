package com.toan.spring.project.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toan.spring.project.dto.BorrowingDetailDto;
import com.toan.spring.project.dto.CheckoutDetailDto;
import com.toan.spring.project.dto.ReaderActionDetailDto;
import com.toan.spring.project.payload.response.MessageResponse;
import com.toan.spring.project.services.BorrowingDetailService;
import com.toan.spring.project.services.UserService;

@RestController
@RequestMapping("/api")
public class BorrowController {
    @Autowired
    private BorrowingDetailService borrowingDetailService;

    // 7 ngày trả sách
    @PostMapping("/user/borrow")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> borrowBook(@RequestParam("userid") Long userid,
            @RequestParam("bookid") Long bookid) {
        try {
            BorrowingDetailDto borrowingDetailDto = borrowingDetailService.borrowBook(userid, bookid,
                    System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000));
            return ResponseEntity.ok(borrowingDetailDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Sách không có hoặc người dùng bị cấm"));
        }

    }

    @PostMapping("/user/return")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> returnBook(@RequestParam("userid") Long userid,
            @RequestParam("bookid") Long bookid) {
        try {
            CheckoutDetailDto checkoutDetailDto = borrowingDetailService.returnBook(userid, bookid);
            return ResponseEntity.ok(checkoutDetailDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Trả sai sách hoặc bạn chưa mượn"));
        }

    }

    @GetMapping("/admin/listborrowdetails")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> readerActionDetails() {
        try {
            List<ReaderActionDetailDto> borrowingDetailDtos = borrowingDetailService.readerActionDetails();
            return ResponseEntity.ok(borrowingDetailDtos);
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Access only ADMIN."));
        }
    }
}
