package com.example.banking.dto;

import com.example.banking.entity.FundTransfer;
import com.example.banking.enums.TransactionStatus;
import com.example.banking.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FundTransferResponse {

    private String transactionId;
    private Long makerId;
    private Long debitAccountId;
    private Long beneficiaryId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String remarks;
    private TransactionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;

    public FundTransferResponse() {
    }

    public static FundTransferResponse from(FundTransfer transfer) {
        FundTransferResponse response = new FundTransferResponse();
        response.transactionId = transfer.getTransactionId();
        response.makerId = transfer.getMakerId();
        response.debitAccountId = transfer.getDebitAccountId();
        response.beneficiaryId = transfer.getBeneficiaryId();
        response.amount = transfer.getAmount();
        response.transactionType = transfer.getTransactionType();
        response.remarks = transfer.getRemarks();
        response.status = transfer.getStatus();
        response.createdAt = transfer.getCreatedAt();
        response.updatedAt = transfer.getUpdatedAt();
        response.approvedBy = transfer.getApprovedBy();
        response.approvedAt = transfer.getApprovedAt();
        response.rejectionReason = transfer.getRejectionReason();
        return response;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getMakerId() {
        return makerId;
    }

    public void setMakerId(Long makerId) {
        this.makerId = makerId;
    }

    public Long getDebitAccountId() {
        return debitAccountId;
    }

    public void setDebitAccountId(Long debitAccountId) {
        this.debitAccountId = debitAccountId;
    }

    public Long getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(Long beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
