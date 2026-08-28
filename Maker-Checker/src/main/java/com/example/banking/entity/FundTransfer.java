package com.example.banking.entity;

import com.example.banking.enums.TransactionStatus;
import com.example.banking.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fund_transfers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false)
    private Long makerId;

    @Column(nullable = false)
    private Long debitAccountId;

    @Column(nullable = false)
    private Long beneficiaryId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(length = 500)
    private String remarks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long approvedBy;

    private LocalDateTime approvedAt;

    @Column(length = 500)
    private String rejectionReason;
}
