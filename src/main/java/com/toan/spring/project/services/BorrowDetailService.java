package com.toan.spring.project.services;

import java.util.List;

import com.toan.spring.project.dto.BorrowDetailDto;
import com.toan.spring.project.dto.ReturnDetailDto;
import com.toan.spring.project.dto.ReaderActionDetailDto;
import com.toan.spring.project.models.Book;
import com.toan.spring.project.models.BorrowDetail;
import com.toan.spring.project.models.User;

public interface BorrowDetailService {
    public BorrowDetail createNewBorrowDetail(User userid, Book bookid, long expectedReturn);

    public BorrowDetail checkOutBorrowDetail(Long userid, Long bookid);

    public BorrowDetail findBorrowingByUserIdAndBookId(Long userid, Long bookid);

    public BorrowDetail findFirstInQueue(Long bookId);

    public BorrowDetail save(BorrowDetail borrowingDetail);

    // borrow
    public BorrowDetailDto borrowBook(Long userid, Long bookid, long expectedReturn);

    // return
    public ReturnDetailDto returnBook(Long userid, Long bookid);

    public List<ReaderActionDetailDto> readerActionDetails();

}
