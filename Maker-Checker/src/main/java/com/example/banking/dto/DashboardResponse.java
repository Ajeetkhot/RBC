package com.example.banking.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardResponse {

    private BigDecimal totalBalance;
    private BigDecimal totalAvailableBalance;
    private int accountCount;

    private List<FundTransferResponse> recentTransactions;
    private List<FundTransferResponse> pendingTransactions;
    private List<FundTransferResponse> pendingApprovals;

    public DashboardResponse() {
    }

    public DashboardResponse(BigDecimal totalBalance, BigDecimal totalAvailableBalance, int accountCount) {
        this.totalBalance = totalBalance;
        this.totalAvailableBalance = totalAvailableBalance;
        this.accountCount = accountCount;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    public BigDecimal getTotalAvailableBalance() {
        return totalAvailableBalance;
    }

    public void setTotalAvailableBalance(BigDecimal totalAvailableBalance) {
        this.totalAvailableBalance = totalAvailableBalance;
    }

    public int getAccountCount() {
        return accountCount;
    }

    public void setAccountCount(int accountCount) {
        this.accountCount = accountCount;
    }

    public List<FundTransferResponse> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<FundTransferResponse> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }

    public List<FundTransferResponse> getPendingTransactions() {
        return pendingTransactions;
    }

    public void setPendingTransactions(List<FundTransferResponse> pendingTransactions) {
        this.pendingTransactions = pendingTransactions;
    }

    public List<FundTransferResponse> getPendingApprovals() {
        return pendingApprovals;
    }

    public void setPendingApprovals(List<FundTransferResponse> pendingApprovals) {
        this.pendingApprovals = pendingApprovals;
    }
}
