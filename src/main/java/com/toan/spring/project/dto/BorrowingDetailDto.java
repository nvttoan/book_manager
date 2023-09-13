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
    private String user;
    private Date borrowTime;
    private Date expectedReturnTime;
    private long penalty;

    public BorrowingDetailDto(BorrowingDetail borrowingDetail) {
        this.book = borrowingDetail.getBook().getTitle();
        this.user = borrowingDetail.getUser().getName();
        this.borrowTime = new Date(borrowingDetail.getBorrowTime());
        this.expectedReturnTime = new Date(borrowingDetail.getExpectedReturnTime());
        this.penalty = borrowingDetail.getPenalty();
    }
}