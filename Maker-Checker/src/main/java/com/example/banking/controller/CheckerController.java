package com.example.banking.controller;

import com.example.banking.dto.ApprovalHistoryResponse;
import com.example.banking.dto.CheckerActionRequest;
import com.example.banking.dto.FundTransferResponse;
import com.example.banking.entity.ApprovalHistory;
import com.example.banking.service.CheckerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CheckerController {

    private final CheckerService checkerService;

    public CheckerController(CheckerService checkerService) {
        this.checkerService = checkerService;
    }

    @GetMapping("/checkers/{checkerId}/pending")
    public List<FundTransferResponse> getPending(@PathVariable Long checkerId) {
        return checkerService.getPending(checkerId);
    }

    @GetMapping("/checkers/{checkerId}/approved")
    public List<FundTransferResponse> getApproved(@PathVariable Long checkerId) {
        return checkerService.getApprovedTransactions(checkerId);
    }

    @GetMapping("/checkers/{checkerId}/transactions/{transactionId}")
    public FundTransferResponse getTransaction(@PathVariable Long checkerId, @PathVariable String transactionId) {
        return checkerService.getTransaction(checkerId, transactionId);
    }

    @PostMapping("/checkers/{checkerId}/approve/{transactionId}")
    public FundTransferResponse approve(@PathVariable Long checkerId,
                                        @PathVariable String transactionId,
                                        @RequestBody CheckerActionRequest request) {
        return checkerService.approve(checkerId, transactionId, request);
    }

    @PostMapping("/checkers/{checkerId}/reject/{transactionId}")
    public FundTransferResponse reject(@PathVariable Long checkerId,
                                       @PathVariable String transactionId,
                                       @RequestBody CheckerActionRequest request) {
        return checkerService.reject(checkerId, transactionId, request);
    }

    @GetMapping("/checkers/{checkerId}/history")
    public List<ApprovalHistoryResponse> getHistory(@PathVariable Long checkerId) {
        return checkerService.getHistory(checkerId)
                .stream()
                .map(ApprovalHistoryResponse::from)
                .collect(Collectors.toList());
    }
}
