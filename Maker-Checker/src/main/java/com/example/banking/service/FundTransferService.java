package com.example.banking.service;

import com.example.banking.dto.CheckerActionRequest;
import com.example.banking.dto.FundTransferRequest;
import com.example.banking.dto.FundTransferResponse;
import com.example.audit.AuditEventRequest;
import com.example.audit.AuditService;
import com.example.banking.entity.Account;
import com.example.banking.entity.ApprovalHistory;
import com.example.banking.entity.Beneficiary;
import com.example.banking.entity.FundTransfer;
import com.example.banking.enums.ApprovalAction;
import com.example.banking.enums.AccountStatus;
import com.example.banking.enums.BeneficiaryStatus;
import com.example.banking.enums.TransactionStatus;
import com.example.banking.enums.TransactionType;
import com.example.banking.exception.BusinessException;
import com.example.banking.exception.ResourceNotFoundException;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.ApprovalHistoryRepository;
import com.example.banking.repository.BeneficiaryRepository;
import com.example.banking.repository.FundTransferRepository;
import com.example.rbc.AuthorizationRequest;
import com.example.rbc.AuthorizationResponse;
import com.example.rbc.AuthorizationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FundTransferService {

    private final FundTransferRepository fundTransferRepository;
    private final AccountRepository accountRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final ApprovalHistoryRepository approvalHistoryRepository;
    private final AuthorizationService authorizationService;
    private final AuditService auditService;

    @Value("${fund.transfer.max-limit:100000}")
    private BigDecimal maxTransactionLimit;

    public FundTransferService(FundTransferRepository fundTransferRepository,
                               AccountRepository accountRepository,
                               BeneficiaryRepository beneficiaryRepository,
                               ApprovalHistoryRepository approvalHistoryRepository,
                               AuthorizationService authorizationService, AuditService auditService) {
        this.fundTransferRepository = fundTransferRepository;
        this.accountRepository = accountRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.approvalHistoryRepository = approvalHistoryRepository;
        this.authorizationService = authorizationService;
        this.auditService = auditService;
    }

    public FundTransferResponse createTransfer(FundTransferRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Amount must be greater than zero");
        }

        if (request.getTransactionType() == null) {
            throw new BusinessException("Transaction type is required");
        }

        AuthorizationResponse authorization = authorizationService.check(new AuthorizationRequest("MAKER_CHECKER", request.getMakerId(),
            "INITIATE_TRANSFER", "BANKING_RESOURCE", java.util.Map.of("amount", request.getAmount())));
        auditService.record(new AuditEventRequest("MAKER_CHECKER", authorization.allowed() ? "ACCESS_ALLOWED" : "ACCESS_DENIED",
            request.getMakerId(), "BANKING_RESOURCE", "TRANSFER_REQUEST", "INITIATE_TRANSFER", authorization.reason(),
            java.util.Map.of("amount", request.getAmount())));
        if (!authorization.allowed()) {
            throw new BusinessException("Maker is not authorized for this transfer amount");
        }

        Account account = accountRepository.findById(request.getDebitAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Debit account not found"));

        if (!account.getUserId().equals(request.getMakerId())) {
            throw new BusinessException("Debit account does not belong to the maker");
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException("Debit account is not active");
        }

        Beneficiary beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId())
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found"));

        if (beneficiary.getStatus() != BeneficiaryStatus.ACTIVE) {
            throw new BusinessException("Beneficiary is inactive");
        }

        if (account.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("Insufficient available balance");
        }

        if (request.getAmount().compareTo(maxTransactionLimit) > 0) {
            throw new BusinessException("Transfer amount exceeds configured limit");
        }

        List<FundTransfer> duplicateCandidates = fundTransferRepository.findByMakerIdAndDebitAccountIdAndBeneficiaryIdAndAmount(
                request.getMakerId(), request.getDebitAccountId(), request.getBeneficiaryId(), request.getAmount());

        boolean duplicatePending = duplicateCandidates.stream()
                .anyMatch(t -> t.getStatus() == TransactionStatus.PENDING_APPROVAL || t.getStatus() == TransactionStatus.APPROVED);

        if (duplicatePending) {
            throw new BusinessException("Duplicate transaction request detected");
        }

        FundTransfer transfer = FundTransfer.builder()
                .transactionId(generateTransactionId())
                .makerId(request.getMakerId())
                .debitAccountId(request.getDebitAccountId())
                .beneficiaryId(request.getBeneficiaryId())
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .remarks(request.getRemarks())
                .status(TransactionStatus.PENDING_APPROVAL)
                .createdAt(LocalDateTime.now())
                .build();

        FundTransfer saved = fundTransferRepository.save(transfer);
        auditService.record(new AuditEventRequest("MAKER_CHECKER", "TRANSFER_INITIATED", saved.getMakerId(),
            "FUND_TRANSFER", saved.getTransactionId(), "INITIATE_TRANSFER", "Fund transfer initiated",
            java.util.Map.of("amount", saved.getAmount(), "beneficiaryId", saved.getBeneficiaryId())));
        return FundTransferResponse.from(saved);
    }

    @Transactional
    public FundTransferResponse editRejectedTransfer(String transactionId, FundTransferRequest request) {
        FundTransfer transfer = fundTransferRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (transfer.getStatus() != TransactionStatus.REJECTED) {
            throw new BusinessException("Only rejected transactions can be edited and resubmitted");
        }

        if (!transfer.getMakerId().equals(request.getMakerId())) {
            throw new BusinessException("The submitted makerId does not belong to the rejected transaction");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Amount must be greater than zero");
        }

        if (request.getTransactionType() == null) {
            throw new BusinessException("Transaction type is required");
        }

        AuthorizationResponse authorization = authorizationService.check(new AuthorizationRequest("MAKER_CHECKER", request.getMakerId(),
            "INITIATE_TRANSFER", "BANKING_RESOURCE", java.util.Map.of("amount", request.getAmount())));
        auditService.record(new AuditEventRequest("MAKER_CHECKER", authorization.allowed() ? "ACCESS_ALLOWED" : "ACCESS_DENIED",
            request.getMakerId(), "BANKING_RESOURCE", transactionId, "INITIATE_TRANSFER", authorization.reason(),
            java.util.Map.of("amount", request.getAmount())));
        if (!authorization.allowed()) {
            throw new BusinessException("Maker is not authorized for this transfer amount");
        }

        Account account = accountRepository.findById(request.getDebitAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Debit account not found"));

        if (!account.getUserId().equals(request.getMakerId())) {
            throw new BusinessException("Debit account does not belong to the maker");
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException("Debit account is not active");
        }

        Beneficiary beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId())
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found"));

        if (beneficiary.getStatus() != BeneficiaryStatus.ACTIVE) {
            throw new BusinessException("Beneficiary is inactive");
        }

        if (account.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("Insufficient available balance");
        }

        if (request.getAmount().compareTo(maxTransactionLimit) > 0) {
            throw new BusinessException("Transfer amount exceeds configured limit");
        }

        transfer.setMakerId(request.getMakerId());
        transfer.setDebitAccountId(request.getDebitAccountId());
        transfer.setBeneficiaryId(request.getBeneficiaryId());
        transfer.setAmount(request.getAmount());
        transfer.setTransactionType(request.getTransactionType());
        transfer.setRemarks(request.getRemarks());
        transfer.setStatus(TransactionStatus.PENDING_APPROVAL);
        transfer.setUpdatedAt(LocalDateTime.now());
        transfer.setApprovedBy(null);
        transfer.setApprovedAt(null);
        transfer.setRejectionReason(null);

        return FundTransferResponse.from(fundTransferRepository.save(transfer));
    }

    public FundTransferResponse getTransferById(String transactionId) {
        FundTransfer transfer = fundTransferRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        return FundTransferResponse.from(transfer);
    }

    public List<FundTransferResponse> getMakerHistory(Long userId) {
        return fundTransferRepository.findByMakerIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(FundTransferResponse::from)
                .collect(Collectors.toList());
    }

    public List<FundTransferResponse> getMakerTransfersByStatus(Long userId, String status) {
        TransactionStatus transactionStatus;
        try {
            transactionStatus = TransactionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid transaction status");
        }

        return fundTransferRepository.findByMakerIdAndStatus(userId, transactionStatus)
                .stream()
                .map(FundTransferResponse::from)
                .collect(Collectors.toList());
    }

    public List<FundTransferResponse> getMakerTransfersByType(Long userId, String type) {
        TransactionType transactionType;
        try {
            transactionType = TransactionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid transaction type");
        }

        return fundTransferRepository.findByMakerIdAndTransactionType(userId, transactionType)
                .stream()
                .map(FundTransferResponse::from)
                .collect(Collectors.toList());
    }

    public List<FundTransferResponse> getPendingByChecker(Long checkerId) {
        return fundTransferRepository.findByStatus(TransactionStatus.PENDING_APPROVAL)
                .stream()
            .filter(transfer -> authorizationService.check(new AuthorizationRequest("MAKER_CHECKER", checkerId,
                "APPROVE_TRANSFER", "BANKING_RESOURCE", java.util.Map.of("makerId", transfer.getMakerId(),
                "amount", transfer.getAmount()))).allowed())
                .map(FundTransferResponse::from)
                .collect(Collectors.toList());
    }

    public FundTransferResponse getCheckerTransaction(Long checkerId, String transactionId) {
        FundTransfer transfer = fundTransferRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        if (transfer.getStatus() != TransactionStatus.PENDING_APPROVAL) {
            throw new BusinessException("Transaction is not pending approval");
        }
        return FundTransferResponse.from(transfer);
    }

    @Transactional
    public FundTransferResponse approveTransaction(Long checkerId, String transactionId, CheckerActionRequest request) {
        FundTransfer transfer = fundTransferRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (transfer.getStatus() != TransactionStatus.PENDING_APPROVAL) {
            throw new BusinessException("Only PENDING_APPROVAL transactions can be approved");
        }

        if (transfer.getMakerId().equals(checkerId)) {
            throw new BusinessException("Maker cannot approve their own transaction");
        }

        AuthorizationResponse authorization = authorizationService.check(new AuthorizationRequest("MAKER_CHECKER", checkerId,
                "APPROVE_TRANSFER", "BANKING_RESOURCE", java.util.Map.of("makerId", transfer.getMakerId(),
            "amount", transfer.getAmount())));
        auditService.record(new AuditEventRequest("MAKER_CHECKER", authorization.allowed() ? "ACCESS_ALLOWED" : "ACCESS_DENIED",
            checkerId, "BANKING_RESOURCE", transfer.getTransactionId(), "APPROVE_TRANSFER", authorization.reason(), null));
        if (!authorization.allowed()) {
            throw new BusinessException("Checker is not authorized for this transaction");
        }

        transfer.setStatus(TransactionStatus.APPROVED);
        transfer.setApprovedBy(checkerId);
        transfer.setApprovedAt(LocalDateTime.now());
        transfer.setUpdatedAt(LocalDateTime.now());
        transfer.setRemarks(request.getRemarks());

        processApprovedTransfer(transfer);

        ApprovalHistory history = ApprovalHistory.builder()
                .transactionId(transfer.getTransactionId())
                .checkerId(checkerId)
                .action(ApprovalAction.APPROVED)
                .remarks(request.getRemarks())
                .createdAt(LocalDateTime.now())
                .build();

        approvalHistoryRepository.save(history);
        auditService.record(new AuditEventRequest("MAKER_CHECKER", "TRANSFER_APPROVED", checkerId,
            "FUND_TRANSFER", transfer.getTransactionId(), "APPROVE_TRANSFER", "Fund transfer approved", null));

        return FundTransferResponse.from(fundTransferRepository.save(transfer));
    }

    @Transactional
    public FundTransferResponse rejectTransaction(Long checkerId, String transactionId, CheckerActionRequest request) {
        FundTransfer transfer = fundTransferRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (transfer.getStatus() != TransactionStatus.PENDING_APPROVAL) {
            throw new BusinessException("Only PENDING_APPROVAL transactions can be rejected");
        }

        if (request.getRejectionReason() == null || request.getRejectionReason().isBlank()) {
            throw new BusinessException("Rejection reason is mandatory");
        }

        transfer.setStatus(TransactionStatus.REJECTED);
        transfer.setUpdatedAt(LocalDateTime.now());
        transfer.setRejectionReason(request.getRejectionReason());

        ApprovalHistory history = ApprovalHistory.builder()
                .transactionId(transfer.getTransactionId())
                .checkerId(checkerId)
                .action(ApprovalAction.REJECTED)
                .remarks(request.getRejectionReason())
                .createdAt(LocalDateTime.now())
                .build();

        approvalHistoryRepository.save(history);
        auditService.record(new AuditEventRequest("MAKER_CHECKER", "TRANSFER_REJECTED", checkerId,
            "FUND_TRANSFER", transfer.getTransactionId(), "REJECT_TRANSFER", "Fund transfer rejected", null));

        return FundTransferResponse.from(fundTransferRepository.save(transfer));
    }

    public List<FundTransferResponse> getApprovedTransactionsForChecker(Long checkerId) {
        return fundTransferRepository.findByApprovedByOrderByApprovedAtDesc(checkerId)
                .stream()
                .filter(txn -> txn.getStatus() == TransactionStatus.APPROVED
                        || txn.getStatus() == TransactionStatus.PROCESSING
                        || txn.getStatus() == TransactionStatus.SUCCESS
                        || txn.getStatus() == TransactionStatus.FAILED)
                .map(FundTransferResponse::from)
                .collect(Collectors.toList());
    }

    public List<FundTransferResponse> getPendingApprovalsForChecker(Long checkerId) {
        return fundTransferRepository.findByStatus(TransactionStatus.PENDING_APPROVAL)
                .stream()
            .filter(transfer -> authorizationService.check(new AuthorizationRequest("MAKER_CHECKER", checkerId,
                "APPROVE_TRANSFER", "BANKING_RESOURCE", java.util.Map.of("makerId", transfer.getMakerId(),
                "amount", transfer.getAmount()))).allowed())
                .map(FundTransferResponse::from)
                .collect(Collectors.toList());
    }

    public List<FundTransferResponse> recentTransactionsForUser(Long userId) {
        return fundTransferRepository.findByMakerIdOrderByCreatedAtDesc(userId)
                .stream()
                .limit(10)
                .map(FundTransferResponse::from)
                .collect(Collectors.toList());
    }

    public List<FundTransferResponse> pendingTransfersForUser(Long userId) {
        return fundTransferRepository.findByMakerIdAndStatus(userId, TransactionStatus.PENDING_APPROVAL)
                .stream()
                .map(FundTransferResponse::from)
                .collect(Collectors.toList());
    }

    private void processApprovedTransfer(FundTransfer transfer) {
        transfer.setStatus(TransactionStatus.PROCESSING);
        transfer.setUpdatedAt(LocalDateTime.now());

        // Simulated processing for this test scope.
        transfer.setStatus(TransactionStatus.SUCCESS);
        transfer.setUpdatedAt(LocalDateTime.now());

        Account account = accountRepository.findById(transfer.getDebitAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Debit account not found"));

        account.setAvailableBalance(account.getAvailableBalance().subtract(transfer.getAmount()));
        account.setBalance(account.getBalance().subtract(transfer.getAmount()));
        accountRepository.save(account);
    }

    private String generateTransactionId() {
        Optional<FundTransfer> last = fundTransferRepository.findAll().stream()
                .max((a, b) -> a.getId().compareTo(b.getId()));

        long next = 100000;
        if (last.isPresent() && last.get().getTransactionId() != null) {
            try {
                next = Long.parseLong(last.get().getTransactionId().replace("TXN", "")) + 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return "TXN" + next;
    }

    public List<FundTransferResponse> getHistoryForChecker(Long checkerId) {
        return approvalHistoryRepository.findByCheckerIdOrderByCreatedAtDesc(checkerId)
                .stream()
                .map(history -> {
                    FundTransferResponse response = new FundTransferResponse();
                    response.setTransactionId(history.getTransactionId());
                    response.setStatus(null);
                    return response;
                })
                .collect(Collectors.toList());
    }
}
