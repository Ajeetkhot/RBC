package com.example.banking.service;

import com.example.banking.dto.BeneficiaryRequest;
import com.example.banking.dto.BeneficiaryResponse;
import com.example.audit.AuditEventRequest;
import com.example.audit.AuditService;
import com.example.banking.entity.Beneficiary;
import com.example.rbc.AuthorizationRequest;
import com.example.rbc.AuthorizationResponse;
import com.example.rbc.AuthorizationService;
import com.example.banking.enums.BeneficiaryStatus;
import com.example.banking.exception.BusinessException;
import com.example.banking.exception.ResourceNotFoundException;
import com.example.banking.repository.BeneficiaryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final AuthorizationService authorizationService;
    private final AuditService auditService;

    public BeneficiaryService(BeneficiaryRepository beneficiaryRepository,
                              AuthorizationService authorizationService, AuditService auditService) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.authorizationService = authorizationService;
        this.auditService = auditService;
    }

    public BeneficiaryResponse createBeneficiary(BeneficiaryRequest request) {
        AuthorizationResponse authorization = authorizationService.check(new AuthorizationRequest("MAKER_CHECKER", request.getCreatedBy(),
            "CREATE_BENEFICIARY", "BANKING_RESOURCE", null));
        auditService.record(new AuditEventRequest("MAKER_CHECKER", authorization.allowed() ? "ACCESS_ALLOWED" : "ACCESS_DENIED",
            request.getCreatedBy(), "BANKING_RESOURCE", "BENEFICIARY", "CREATE_BENEFICIARY", authorization.reason(), null));
        if (!authorization.allowed()) {
            throw new BusinessException("Not authorized to create a beneficiary");
        }
        Beneficiary beneficiary = Beneficiary.builder()
                .beneficiaryName(request.getBeneficiaryName())
                .accountNumber(request.getAccountNumber())
                .bankName(request.getBankName())
                .ifscCode(request.getIfscCode())
                .createdBy(request.getCreatedBy())
                .status(BeneficiaryStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        Beneficiary saved = beneficiaryRepository.save(beneficiary);
        auditService.record(new AuditEventRequest("MAKER_CHECKER", "BENEFICIARY_CREATED", request.getCreatedBy(),
            "BENEFICIARY", String.valueOf(saved.getId()), "CREATE_BENEFICIARY", "Beneficiary created", null));
        return BeneficiaryResponse.from(saved);
    }

    public List<BeneficiaryResponse> getAllBeneficiaries() {
        return beneficiaryRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(BeneficiaryResponse::from)
                .collect(Collectors.toList());
    }

    public List<BeneficiaryResponse> getBeneficiariesForUser(Long userId) {
        return beneficiaryRepository.findByCreatedBy(userId)
                .stream()
                .map(BeneficiaryResponse::from)
                .collect(Collectors.toList());
    }

    public BeneficiaryResponse getBeneficiary(Long beneficiaryId) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found"));
        return BeneficiaryResponse.from(beneficiary);
    }

    public BeneficiaryResponse updateBeneficiary(Long beneficiaryId, BeneficiaryRequest request) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found"));

        beneficiary.setBeneficiaryName(request.getBeneficiaryName());
        beneficiary.setAccountNumber(request.getAccountNumber());
        beneficiary.setBankName(request.getBankName());
        beneficiary.setIfscCode(request.getIfscCode());
        beneficiary.setUpdatedAt(LocalDateTime.now());

        return BeneficiaryResponse.from(beneficiaryRepository.save(beneficiary));
    }

    public BeneficiaryResponse activateBeneficiary(Long beneficiaryId) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found"));
        beneficiary.setStatus(BeneficiaryStatus.ACTIVE);
        beneficiary.setUpdatedAt(LocalDateTime.now());
        return BeneficiaryResponse.from(beneficiaryRepository.save(beneficiary));
    }

    public BeneficiaryResponse deactivateBeneficiary(Long beneficiaryId) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found"));
        beneficiary.setStatus(BeneficiaryStatus.INACTIVE);
        beneficiary.setUpdatedAt(LocalDateTime.now());
        return BeneficiaryResponse.from(beneficiaryRepository.save(beneficiary));
    }

    public Beneficiary validateActiveBeneficiary(Long beneficiaryId) {
        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new BusinessException("Beneficiary does not exist"));
        if (beneficiary.getStatus() != BeneficiaryStatus.ACTIVE) {
            throw new BusinessException("Beneficiary is inactive");
        }
        return beneficiary;
    }
}
