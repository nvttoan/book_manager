package com.toan.spring.project.services.impl;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import com.toan.spring.project.common.BookStatus;
import com.toan.spring.project.common.BorrowStatus;
import com.toan.spring.project.common.ReaderAction;
import com.toan.spring.project.dto.ReaderActionDetailDto;
import com.toan.spring.project.models.Book;
import com.toan.spring.project.models.BorrowingDetail;
import com.toan.spring.project.models.User;
import com.toan.spring.project.repository.BorrowingDetailRepository;
import com.toan.spring.project.services.BookService;
import com.toan.spring.project.services.BorrowingDetailService;

@Service
@EnableCaching

public class BorrowingDetailServiceImpl implements BorrowingDetailService {
    @Autowired
    private BorrowingDetailRepository borrowingDetailRepository;

    @Autowired
    private BookService bookService;

    @Override
    public BorrowingDetail createNewBorrowDetail(User userid, Book bookid, long expectedReturn) {
        BorrowStatus status = (bookid.getStatus() == BookStatus.NOT_AVAILABLE
                ? BorrowStatus.IN_QUEUE
                : BorrowStatus.BORROWING);

        BorrowingDetail borrowingDetail = BorrowingDetail.builder()
                .book(bookid)
                .user(userid)
                .borrowTime(System.currentTimeMillis())
                .expectedReturnTime(expectedReturn)
                .status(status)
                .build();
        return borrowingDetailRepository.save(borrowingDetail);
    }

    @Override
    public BorrowingDetail checkOutBorrowDetail(Long userid, Long bookid) {
        BorrowingDetail borrowingDetail = findBorrowingByUserIdAndBookId(userid, bookid);

        long current = System.currentTimeMillis();
        long penaltyDuration = current - borrowingDetail.getExpectedReturnTime();
        borrowingDetail.setPenalty((int) (penaltyDuration > 0 ? penaltyDuration : 0) / (24 * 60 * 60 * 1000) * 1000);
        borrowingDetail.setStatus(BorrowStatus.RETURNED);
        BorrowingDetail ret = save(borrowingDetail);

        Book book = bookService.findByIdAndStatus(bookid, BookStatus.NOT_AVAILABLE);
        book.setStatus(BookStatus.AVAILABLE);
        bookService.save(book);

        borrowingDetail = findFirstInQueue(bookid);
        if (borrowingDetail != null) {
            book.setStatus(BookStatus.NOT_AVAILABLE);
            bookService.save(book);
            borrowingDetail.setStatus(BorrowStatus.BORROWING);
            borrowingDetail.setExpectedReturnTime(current + (7 * 24 * 60 * 60 * 1000));
            save(borrowingDetail);
        }
        return ret;
    }

    // find user đang mượn
    @Override
    public BorrowingDetail findBorrowingByUserIdAndBookId(Long userid, Long bookid) {
        return borrowingDetailRepository.findByUserIdAndBookIdAndStatus(userid, bookid, BorrowStatus.BORROWING)
                .orElse(null);
    }

    @Override
    public BorrowingDetail findFirstInQueue(Long bookId) {
        return borrowingDetailRepository
                .findFirstByBookIdAndStatusOrderByBorrowTimeDesc(bookId, BorrowStatus.IN_QUEUE)
                .orElse(null);
    }

    @Override
    public BorrowingDetail save(BorrowingDetail borrowingDetail) {
        return borrowingDetailRepository.save(borrowingDetail);
    }

    @Override
    // @Cacheable("users")
    public List<ReaderActionDetailDto> readerActionDetails() {
        List<BorrowingDetail> borrowingDetails = borrowingDetailRepository.findAll();
        List<ReaderActionDetailDto> readerActionDetailDtos = new ArrayList<>();
        for (BorrowingDetail borrowingDetail : borrowingDetails) {
            if (borrowingDetail.getStatus() == BorrowStatus.RETURNED) {
                readerActionDetailDtos.add(new ReaderActionDetailDto(borrowingDetail, ReaderAction.RETURN));
            }

            readerActionDetailDtos.add(new ReaderActionDetailDto(borrowingDetail, ReaderAction.BORROW));
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
