package com.toan.spring.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.toan.spring.project.common.BorrowStatus;
import com.toan.spring.project.models.BorrowDetail;

@Repository
public interface BorrowDetailRepository extends JpaRepository<BorrowDetail, Long> {
    public Optional<BorrowDetail> findByUserIdAndBookIdAndStatus(Long userid, Long bookid, BorrowStatus status);

    public Optional<BorrowDetail> findFirstByBookIdAndStatusOrderByBorrowTimeDesc(Long bookid,
            BorrowStatus status);
}