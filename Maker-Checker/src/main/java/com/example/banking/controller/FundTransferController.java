package com.example.banking.controller;

import com.example.banking.dto.CheckerActionRequest;
import com.example.banking.dto.FundTransferRequest;
import com.example.banking.dto.FundTransferResponse;
import com.example.banking.entity.ApprovalHistory;
import com.example.banking.service.CheckerService;
import com.example.banking.service.FundTransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api", ""})
public class FundTransferController {

    private final FundTransferService fundTransferService;

    public FundTransferController(FundTransferService fundTransferService) {
        this.fundTransferService = fundTransferService;
    }

    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public FundTransferResponse createTransfer(@Valid @RequestBody FundTransferRequest request) {
        return fundTransferService.createTransfer(request);
    }

    @PutMapping("/transfers/{transactionId}")
    public FundTransferResponse editRejectedTransfer(@PathVariable String transactionId,
                                                     @Valid @RequestBody FundTransferRequest request) {
        return fundTransferService.editRejectedTransfer(transactionId, request);
    }

    @GetMapping("/transfers/{transactionId}")
    public FundTransferResponse getTransferById(@PathVariable String transactionId) {
        return fundTransferService.getTransferById(transactionId);
    }

    @GetMapping("/transfers/user/{userId}/history")
    public List<FundTransferResponse> getMakerHistory(@PathVariable Long userId) {
        return fundTransferService.getMakerHistory(userId);
    }

    @GetMapping("/transfers/user/{userId}/status/{status}")
    public List<FundTransferResponse> getTransfersByStatus(@PathVariable Long userId, @PathVariable String status) {
        return fundTransferService.getMakerTransfersByStatus(userId, status);
    }

    @GetMapping("/transfers/user/{userId}/type/{transactionType}")
    public List<FundTransferResponse> getTransfersByType(@PathVariable Long userId, @PathVariable String transactionType) {
        return fundTransferService.getMakerTransfersByType(userId, transactionType);
    }
}
