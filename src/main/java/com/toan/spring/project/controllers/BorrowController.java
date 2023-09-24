package com.toan.spring.project.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.toan.spring.project.dto.BorrowDetailDto;
import com.toan.spring.project.dto.ReturnDetailDto;
import com.toan.spring.project.dto.ReaderActionDetailDto;
import com.toan.spring.project.payload.response.StringResponse;
import com.toan.spring.project.payload.response.ObjectResponse;
import com.toan.spring.project.services.BorrowDetailService;

@RestController
@RequestMapping("/api")
public class BorrowController {
    @Autowired
    private BorrowDetailService borrowDetailService;

    // 7 ngày trả sách
    @PostMapping("/user/borrow")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ObjectResponse> borrowBook(@RequestParam("userid") Long userid,
            @RequestParam("bookid") Long bookid) {
        try {
            if (userid == null || bookid == null) {
                return ResponseEntity.badRequest().body(new ObjectResponse(1, "Thiếu id của sách hoặc user"));
            }
            BorrowDetailDto borrowDetailDto = borrowDetailService.borrowBook(userid, bookid,
                    System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000));
            return ResponseEntity.ok(new ObjectResponse(0, borrowDetailDto));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ObjectResponse(2, "Thực hiện thất bại: " + e.getMessage()));
        }
    }

    @PostMapping("/user/return")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> returnBook(@RequestParam("userid") Long userid,
            @RequestParam("bookid") Long bookid) {
        // try {
        ReturnDetailDto checkoutDetailDto = borrowDetailService.returnBook(userid, bookid);
        return ResponseEntity.ok(new ObjectResponse(0, checkoutDetailDto));

        // } catch (Exception e) {
        // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        // .body(new StringResponse(1,
        // "Thực hiện thất bại: Bạn chưa mượn hoặc đã trả sách " + e.getMessage()));
        // }

    }

    @GetMapping("/admin/listborrowdetails")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> readerActionDetails() {
        try {
            List<ReaderActionDetailDto> borrowingDetailDtos = borrowDetailService.readerActionDetails();
            return ResponseEntity.ok(new ObjectResponse(0, borrowingDetailDtos));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StringResponse(1, "Thực hiện thất bại: " + e.getMessage()));
        }
    }
}
