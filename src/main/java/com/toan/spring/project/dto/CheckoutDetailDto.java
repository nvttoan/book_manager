package com.toan.spring.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.toan.spring.project.models.BorrowingDetail;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutDetailDto {
    private String book;
    private String author;
    private String username;
    private String name;
    private Date borrowTime;
    private Date expectedReturnTime;
    private Date returnTime;
    private long penaltyTime;
    private int penaltyMoney;

    public CheckoutDetailDto(BorrowingDetail borrowingDetail, long returnTime) {
        this.book = borrowingDetail.getBook().getTitle();
        this.author = borrowingDetail.getBook().getAuthor();
        this.username = borrowingDetail.getUser().getUsername();
        this.name = borrowingDetail.getUser().getName();
        this.borrowTime = new Date(borrowingDetail.getBorrowTime());
        this.expectedReturnTime = new Date(borrowingDetail.getExpectedReturnTime());
        this.returnTime = new Date(returnTime);
        this.penaltyTime = returnTime - expectedReturnTime.getTime();
        if (borrowingDetail.getPenalty() > 0) {
            this.penaltyMoney = (int) borrowingDetail.getPenalty();
        } else {
            this.penaltyMoney = 0;
        }

    }
}