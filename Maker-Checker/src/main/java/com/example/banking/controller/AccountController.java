package com.example.banking.controller;

import com.example.banking.dto.AccountResponse;
import com.example.banking.dto.AccountRequest;
import com.example.banking.dto.FundTransferResponse;
import com.example.banking.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("UP");
    }

    @PostMapping("/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@Valid @RequestBody AccountRequest request) {
        return accountService.createAccount(request);
    }

    @GetMapping("/accounts/user/{userId}")
    public List<AccountResponse> getAccountsByUser(@PathVariable Long userId) {
        return accountService.getAccountsForUser(userId);
    }

    @GetMapping("/accounts/{accountId}")
    public AccountResponse getAccount(@PathVariable Long accountId) {
        return accountService.getAccount(accountId);
    }

    @GetMapping("/accounts/{accountId}/transactions")
    public List<FundTransferResponse> getRecentTransactionsForAccount(@PathVariable Long accountId) {
        return accountService.getRecentTransactionsForAccount(accountId);
    }
}
