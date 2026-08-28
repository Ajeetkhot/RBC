package com.example.banking.service;

import com.example.banking.dto.AccountResponse;
import com.example.banking.dto.AccountRequest;
import com.example.banking.dto.FundTransferResponse;
import com.example.banking.entity.Account;
import com.example.banking.entity.FundTransfer;
import com.example.banking.exception.ResourceNotFoundException;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.FundTransferRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final FundTransferRepository fundTransferRepository;

    public AccountService(AccountRepository accountRepository, FundTransferRepository fundTransferRepository) {
        this.accountRepository = accountRepository;
        this.fundTransferRepository = fundTransferRepository;
    }

    public List<AccountResponse> getAccountsForUser(Long userId) {
        return accountRepository.findByUserId(userId)
                .stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());
    }

    public AccountResponse createAccount(AccountRequest request) {
        if (request.getAvailableBalance().compareTo(request.getBalance()) > 0) {
            throw new IllegalArgumentException("Available balance cannot exceed balance");
        }
        Account account = Account.builder()
                .accountNumber(request.getAccountNumber())
                .accountType(request.getAccountType())
                .currency(request.getCurrency())
                .balance(request.getBalance())
                .availableBalance(request.getAvailableBalance())
                .status(com.example.banking.enums.AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .userId(request.getUserId())
                .build();
        return AccountResponse.from(accountRepository.save(account));
    }

    public AccountResponse getAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        return AccountResponse.from(account);
    }

    public List<FundTransferResponse> getRecentTransactionsForAccount(Long accountId) {
        List<FundTransfer> transfers = fundTransferRepository.findByDebitAccountIdOrderByCreatedAtDesc(accountId);
        return transfers.stream().map(FundTransferResponse::from).collect(Collectors.toList());
    }
}
