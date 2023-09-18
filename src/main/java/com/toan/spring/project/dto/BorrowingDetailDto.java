package com.toan.spring.project.dto;

import lombok.*;

import java.util.Date;

import com.toan.spring.project.models.BorrowingDetail;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingDetailDto {
    private String book;
    private String author;

    private String username;
    private Date borrowTime;
    private Date expectedReturnTime;
    private long penaltyMoney;

    // constructor tạo dto từ detail
    public BorrowingDetailDto(BorrowingDetail borrowingDetail) {
        this.book = borrowingDetail.getBook().getTitle();
        this.author = borrowingDetail.getBook().getAuthor();
        this.username = borrowingDetail.getUser().getName();
        this.borrowTime = new Date(borrowingDetail.getBorrowTime());
        this.expectedReturnTime = new Date(borrowingDetail.getExpectedReturnTime());
        this.penaltyMoney = borrowingDetail.getPenalty();
    }
}