package com.example.banking.service;

import com.example.banking.dto.CheckerActionRequest;
import com.example.banking.dto.FundTransferResponse;
import com.example.banking.entity.ApprovalHistory;
import com.example.banking.repository.ApprovalHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CheckerService {

    private final FundTransferService fundTransferService;
    private final ApprovalHistoryRepository approvalHistoryRepository;

    public CheckerService(FundTransferService fundTransferService,
                          ApprovalHistoryRepository approvalHistoryRepository) {
        this.fundTransferService = fundTransferService;
        this.approvalHistoryRepository = approvalHistoryRepository;
    }

    public List<FundTransferResponse> getPending(Long checkerId) {
        return fundTransferService.getPendingByChecker(checkerId);
    }

    public FundTransferResponse getTransaction(Long checkerId, String transactionId) {
        return fundTransferService.getCheckerTransaction(checkerId, transactionId);
    }

    public FundTransferResponse approve(Long checkerId, String transactionId, CheckerActionRequest request) {
        return fundTransferService.approveTransaction(checkerId, transactionId, request);
    }

    public FundTransferResponse reject(Long checkerId, String transactionId, CheckerActionRequest request) {
        return fundTransferService.rejectTransaction(checkerId, transactionId, request);
    }

    public List<FundTransferResponse> getApprovedTransactions(Long checkerId) {
        return fundTransferService.getApprovedTransactionsForChecker(checkerId);
    }

    public List<ApprovalHistory> getHistory(Long checkerId) {
        return approvalHistoryRepository.findByCheckerIdOrderByCreatedAtDesc(checkerId);
    }
}
