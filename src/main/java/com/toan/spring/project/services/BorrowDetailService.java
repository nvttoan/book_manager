package com.toan.spring.project.services;

import java.util.List;

import com.toan.spring.project.dto.BorrowDetailDto;
import com.toan.spring.project.dto.ReturnDetailDto;
import com.toan.spring.project.dto.ReaderActionDetailDto;
import com.toan.spring.project.models.Book;
import com.toan.spring.project.models.BorrowingDetail;
import com.toan.spring.project.models.User;

public interface BorrowDetailService {
    public BorrowingDetail createNewBorrowDetail(User userid, Book bookid, long expectedReturn);

    public BorrowingDetail checkOutBorrowDetail(Long userid, Long bookid);

    public BorrowingDetail findBorrowingByUserIdAndBookId(Long userid, Long bookid);

    public BorrowingDetail findFirstInQueue(Long bookId);

    public BorrowingDetail save(BorrowingDetail borrowingDetail);

    // borrow
    public BorrowDetailDto borrowBook(Long userid, Long bookid, long expectedReturn);

    // return
    public ReturnDetailDto returnBook(Long userid, Long bookid);

    public List<ReaderActionDetailDto> readerActionDetails();

}