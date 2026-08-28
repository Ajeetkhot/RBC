package com.example.banking.repository;

import com.example.banking.entity.FundTransfer;
import com.example.banking.enums.TransactionStatus;
import com.example.banking.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FundTransferRepository extends JpaRepository<FundTransfer, Long> {

    Optional<FundTransfer> findByTransactionId(String transactionId);

    List<FundTransfer> findByMakerIdOrderByCreatedAtDesc(Long makerId);

    List<FundTransfer> findByMakerIdAndStatus(Long makerId, TransactionStatus status);

    List<FundTransfer> findByMakerIdAndTransactionType(Long makerId, TransactionType transactionType);

    List<FundTransfer> findByStatus(TransactionStatus status);

    List<FundTransfer> findByApprovedByOrderByApprovedAtDesc(Long checkerId);

    List<FundTransfer> findByDebitAccountIdOrderByCreatedAtDesc(Long debitAccountId);

    List<FundTransfer> findByMakerIdAndDebitAccountIdAndBeneficiaryIdAndAmount(Long makerId, Long debitAccountId, Long beneficiaryId, java.math.BigDecimal amount);
}
