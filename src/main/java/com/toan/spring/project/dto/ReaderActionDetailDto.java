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

    public ReaderActionDetailDto(BorrowDetail detail, ReaderAction readerAction) {
        this.readerAction = readerAction;
        if (readerAction == ReaderAction.BORROW) {
            this.time = new Date(detail.getBorrowTime());
        } else {
            this.time = new Date(detail.getExpectedReturnTime() + detail.getPenalty() * 24 * 60 * 60);
        }
        this.user = detail.getUser().getUsername();
        this.name = detail.getUser().getName();
    }
}
