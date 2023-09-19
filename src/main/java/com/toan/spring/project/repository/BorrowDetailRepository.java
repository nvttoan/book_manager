package com.toan.spring.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toan.spring.project.common.BorrowStatus;
import com.toan.spring.project.models.BorrowingDetail;

@Repository
public interface BorrowDetailRepository extends JpaRepository<BorrowingDetail, Long> {
    public Optional<BorrowingDetail> findByUserIdAndBookIdAndStatus(Long userid, Long bookid, BorrowStatus status);

    public Optional<BorrowingDetail> findFirstByBookIdAndStatusOrderByBorrowTimeDesc(Long bookid,
            BorrowStatus status);
}