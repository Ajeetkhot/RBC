package com.example.banking.dto;

import com.example.banking.entity.ApprovalHistory;
import com.example.banking.enums.ApprovalAction;

import java.time.LocalDateTime;

public class ApprovalHistoryResponse {

    private String transactionId;
    private ApprovalAction action;
    private String remarks;
    private LocalDateTime createdAt;

    public ApprovalHistoryResponse() {
    }

    public ApprovalHistoryResponse(String transactionId, ApprovalAction action, String remarks, LocalDateTime createdAt) {
        this.transactionId = transactionId;
        this.action = action;
        this.remarks = remarks;
        this.createdAt = createdAt;
    }

    public static ApprovalHistoryResponse from(ApprovalHistory history) {
        return new ApprovalHistoryResponse(history.getTransactionId(), history.getAction(), history.getRemarks(), history.getCreatedAt());
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public ApprovalAction getAction() {
        return action;
    }

    public void setAction(ApprovalAction action) {
        this.action = action;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
