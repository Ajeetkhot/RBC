package com.example.banking.service;

import com.example.banking.dto.FundTransferResponse;
import com.example.banking.entity.Account;
import com.example.banking.entity.FundTransfer;
import com.example.banking.enums.TransactionStatus;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.FundTransferRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final AccountRepository accountRepository;
    private final FundTransferRepository fundTransferRepository;

    public DashboardService(AccountRepository accountRepository, FundTransferRepository fundTransferRepository) {
        this.accountRepository = accountRepository;
        this.fundTransferRepository = fundTransferRepository;
    }

    public DashboardAccountSummary getAccountSummary(Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);
        BigDecimal totalBalance = accounts.stream().map(Account::getBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAvailableBalance = accounts.stream().map(Account::getAvailableBalance).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardAccountSummary(totalBalance, totalAvailableBalance, accounts.size());
    }

    public List<FundTransferResponse> getRecentTransactions(Long userId) {
        return fundTransferRepository.findByMakerIdOrderByCreatedAtDesc(userId)
                .stream()
                .limit(10)
                .map(FundTransferResponse::from)
                .collect(Collectors.toList());
    }

    public List<FundTransferResponse> getPendingTransactions(Long userId) {
        return fundTransferRepository.findByMakerIdAndStatus(userId, TransactionStatus.PENDING_APPROVAL)
                .stream()
                .map(FundTransferResponse::from)
                .collect(Collectors.toList());
    }

    public List<FundTransferResponse> getPendingApprovalsForChecker(Long checkerId) {
        return fundTransferRepository.findByStatus(TransactionStatus.PENDING_APPROVAL)
                .stream()
                .map(FundTransferResponse::from)
                .collect(Collectors.toList());
    }

    public static class DashboardAccountSummary {
        private BigDecimal totalBalance;
        private BigDecimal totalAvailableBalance;
        private int numberOfAccounts;

        public DashboardAccountSummary(BigDecimal totalBalance, BigDecimal totalAvailableBalance, int numberOfAccounts) {
            this.totalBalance = totalBalance;
            this.totalAvailableBalance = totalAvailableBalance;
            this.numberOfAccounts = numberOfAccounts;
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

        public int getNumberOfAccounts() {
            return numberOfAccounts;
        }

        public void setNumberOfAccounts(int numberOfAccounts) {
            this.numberOfAccounts = numberOfAccounts;
        }
    }
}
