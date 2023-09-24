package com.toan.spring.project.dto;

import java.util.Date;

import com.toan.spring.project.common.ReaderAction;
import com.toan.spring.project.models.BorrowDetail;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReaderActionDetailDto {
    private ReaderAction readerAction;
    private Date time;
    private String user;
    private String name;
    private String book;
    private int penaltyMoney;

    public ReaderActionDetailDto(BorrowDetail borrowDetail, ReaderAction readerAction) {
        this.readerAction = readerAction;
        if (readerAction == ReaderAction.BORROW) {
            this.time = new Date(borrowDetail.getBorrowTime());
        } else {
            this.time = new Date(borrowDetail.getExpectedReturnTime() + borrowDetail.getPenalty() * 24 * 60 * 60);
        }
        this.user = borrowDetail.getUser().getUsername();
        this.name = borrowDetail.getUser().getName();
        this.book = borrowDetail.getBook().getTitle();
        if (borrowDetail.getPenalty() > 0) {
            this.penaltyMoney = (int) borrowDetail.getPenalty();
        } else {
            this.penaltyMoney = 0;
        }
    }
}
