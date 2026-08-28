package com.example.banking.dto;

import com.example.banking.entity.Account;
import com.example.banking.enums.AccountStatus;
import com.example.banking.enums.AccountType;

import java.math.BigDecimal;

public class AccountResponse {

    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private String currency;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private AccountStatus status;

    public AccountResponse() {
    }

    public AccountResponse(Long id, String accountNumber, AccountType accountType, String currency,
                           BigDecimal balance, BigDecimal availableBalance, AccountStatus status) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.currency = currency;
        this.balance = balance;
        this.availableBalance = availableBalance;
        this.status = status;
    }

    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getAccountNumber(), account.getAccountType(),
                account.getCurrency(), account.getBalance(), account.getAvailableBalance(), account.getStatus());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
