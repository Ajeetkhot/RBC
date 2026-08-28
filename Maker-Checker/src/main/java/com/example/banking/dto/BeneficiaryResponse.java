package com.example.banking.dto;

import com.example.banking.entity.Beneficiary;
import com.example.banking.enums.BeneficiaryStatus;

public class BeneficiaryResponse {

    private Long id;
    private String beneficiaryName;
    private String accountNumber;
    private String bankName;
    private String ifscCode;
    private Long createdBy;
    private BeneficiaryStatus status;

    public BeneficiaryResponse() {
    }

    public BeneficiaryResponse(Long id, String beneficiaryName, String accountNumber, String bankName,
                               String ifscCode, Long createdBy, BeneficiaryStatus status) {
        this.id = id;
        this.beneficiaryName = beneficiaryName;
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.ifscCode = ifscCode;
        this.createdBy = createdBy;
        this.status = status;
    }

    public static BeneficiaryResponse from(Beneficiary beneficiary) {
        return new BeneficiaryResponse(beneficiary.getId(), beneficiary.getBeneficiaryName(),
                beneficiary.getAccountNumber(), beneficiary.getBankName(), beneficiary.getIfscCode(),
                beneficiary.getCreatedBy(), beneficiary.getStatus());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public BeneficiaryStatus getStatus() {
        return status;
    }

    public void setStatus(BeneficiaryStatus status) {
        this.status = status;
    }
}
