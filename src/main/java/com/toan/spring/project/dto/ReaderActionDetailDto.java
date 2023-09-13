package com.toan.spring.project.dto;

import java.util.Date;

import com.toan.spring.project.common.ReaderAction;
import com.toan.spring.project.models.BorrowingDetail;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReaderActionDetailDto {
    private ReaderAction readerAction;
    private Date time;
    private String user;
    private String name;

    public ReaderActionDetailDto(BorrowingDetail detail, ReaderAction readerAction) {
        this.readerAction = readerAction;
        this.time = new Date((readerAction == ReaderAction.BORROW
                ? detail.getBorrowTime()
                : detail.getExpectedReturnTime() + detail.getPenalty()));
        this.user = detail.getUser().getUsername();
        this.name = detail.getUser().getName();
    }
}
