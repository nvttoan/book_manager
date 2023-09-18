package com.toan.spring.project.models;

import com.toan.spring.project.common.BorrowStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "user_borrow")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowingDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;
    // khóa ngoại là bảng book và user
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @NotNull
    @Column(name = "borrow_time")
    private long borrowTime;

    @NotNull
    @Column(name = "expected_return_time")
    private long expectedReturnTime;

    @NotNull
    @Column(name = "penalty")
    private long penalty;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16, nullable = false)
    private BorrowStatus status;
}
