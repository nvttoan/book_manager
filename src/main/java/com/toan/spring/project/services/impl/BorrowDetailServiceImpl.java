package com.toan.spring.project.services.impl;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import com.toan.spring.project.common.BookStatus;
import com.toan.spring.project.common.BorrowStatus;
import com.toan.spring.project.common.ReaderAction;
import com.toan.spring.project.dto.BorrowDetailDto;
import com.toan.spring.project.dto.ReturnDetailDto;
import com.toan.spring.project.dto.ReaderActionDetailDto;
import com.toan.spring.project.exception.ResourceNotFoundException;
import com.toan.spring.project.models.Book;
import com.toan.spring.project.models.BorrowDetail;
import com.toan.spring.project.models.User;
import com.toan.spring.project.repository.BorrowDetailRepository;
import com.toan.spring.project.repository.UserRepository;
import com.toan.spring.project.services.BookService;
import com.toan.spring.project.services.BorrowDetailService;

@Service
@EnableCaching

public class BorrowDetailServiceImpl implements BorrowDetailService {
    @Autowired
    private BorrowDetailRepository borrowDetailRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookService bookService;

    @Override
    public BorrowDetail NewBorrowDetail(User userid, Book bookid, long expectedReturn) {
        BorrowStatus status = (bookid.getStatus() == BookStatus.NOT_AVAILABLE
                ? BorrowStatus.IN_QUEUE
                : BorrowStatus.BORROWING);

        BorrowDetail borrowDetail = BorrowDetail.builder()
                .book(bookid)
                .user(userid)
                .borrowTime(System.currentTimeMillis())
                .expectedReturnTime(expectedReturn)
                .status(status)
                .build();
        return borrowDetailRepository.save(borrowDetail);
    }

    @Override
    public BorrowDetail ReturnDetail(Long userid, Long bookid) {
        BorrowDetail borrowDetail = findBorrowingByUserIdAndBookId(userid, bookid);

        long current = System.currentTimeMillis();
        long penaltyDuration = current - borrowDetail.getExpectedReturnTime();
        borrowDetail.setPenalty((int) (penaltyDuration > 0 ? penaltyDuration : 0) / (24 * 60 * 60 * 1000) * 1000);
        borrowDetail.setStatus(BorrowStatus.RETURNED);
        BorrowDetail ret = save(borrowDetail);

        Book book = bookService.findByIdAndStatus(bookid, BookStatus.NOT_AVAILABLE);
        book.setStatus(BookStatus.AVAILABLE);
        bookService.save(book);

        borrowDetail = findFirstInQueue(bookid);
        if (borrowDetail != null) {
            book.setStatus(BookStatus.NOT_AVAILABLE);
            bookService.save(book);
            borrowDetail.setStatus(BorrowStatus.BORROWING);
            borrowDetail.setBorrowTime(System.currentTimeMillis());
            borrowDetail.setExpectedReturnTime(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000));
            save(borrowDetail);
        }
        return ret;
    }

    // find user đang mượn
    @Override
    public BorrowDetail findBorrowingByUserIdAndBookId(Long userid, Long bookid) {
        return borrowDetailRepository.findByUserIdAndBookIdAndStatus(userid, bookid, BorrowStatus.BORROWING)
                .orElse(null);
    }

    @Override
    public BorrowDetail findFirstInQueue(Long bookId) {
        return borrowDetailRepository
                .findFirstByBookIdAndStatusOrderByBorrowTimeDesc(bookId, BorrowStatus.IN_QUEUE)
                .orElse(null);
    }

    @Override
    public BorrowDetail save(BorrowDetail borrowDetail) {
        return borrowDetailRepository.save(borrowDetail);
    }

    // borrow
    @Override
    public BorrowDetailDto borrowBook(Long userid, Long bookid, long expectedReturn) {
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new ResourceNotFoundException("Unavailable user"));
        Book book = bookService.findById(bookid);
        BorrowDetail borrowDetail = this.NewBorrowDetail(user, book, expectedReturn);
        if (book.getStatus() == BookStatus.AVAILABLE) {
            book.setStatus(BookStatus.NOT_AVAILABLE);
            bookService.save(book);
        }
        return new BorrowDetailDto(borrowDetail);
    }

    // return
    @Override
    public ReturnDetailDto returnBook(Long userid, Long bookid) {
        BorrowDetail borrowDetail = this.ReturnDetail(userid, bookid);
        return new ReturnDetailDto(borrowDetail, System.currentTimeMillis());
    }

    @Override
    public List<ReaderActionDetailDto> readerActionDetails() {
        List<BorrowDetail> borrowDetails = borrowDetailRepository.findAll();
        List<ReaderActionDetailDto> readerActionDetailDtos = new ArrayList<>();
        for (BorrowDetail borrowDetail : borrowDetails) {
            if (borrowDetail.getStatus() == BorrowStatus.RETURNED) {
                readerActionDetailDtos.add(new ReaderActionDetailDto(borrowDetail, ReaderAction.RETURN));
            }

            readerActionDetailDtos.add(new ReaderActionDetailDto(borrowDetail, ReaderAction.BORROW));
        }
        Collections.sort(readerActionDetailDtos, new Comparator<ReaderActionDetailDto>() {
            @Override
            public int compare(ReaderActionDetailDto o1, ReaderActionDetailDto o2) {
                return o1.getTime().compareTo(o2.getTime());
            }
        });
        return readerActionDetailDtos;
    }
}