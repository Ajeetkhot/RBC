package com.example.banking.dto;

import com.example.banking.enums.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AccountRequest {
    @NotBlank private String accountNumber;
    @NotNull private AccountType accountType;
    @NotBlank private String currency;
    @NotNull @DecimalMin("0.00") private BigDecimal balance;
    @NotNull @DecimalMin("0.00") private BigDecimal availableBalance;
    @NotNull private Long userId;

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}