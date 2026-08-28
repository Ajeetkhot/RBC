package com.example.banking.controller;

import com.example.banking.dto.FundTransferResponse;
import com.example.banking.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/{userId}/accounts")
    public Map<String, Object> getDashboardAccountSummary(@PathVariable Long userId) {
        DashboardService.DashboardAccountSummary summary = dashboardService.getAccountSummary(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("totalBalance", summary.getTotalBalance());
        result.put("totalAvailableBalance", summary.getTotalAvailableBalance());
        result.put("numberOfAccounts", summary.getNumberOfAccounts());
        return result;
    }

    @GetMapping("/dashboard/{userId}/recent-transactions")
    public List<FundTransferResponse> getRecentTransactions(@PathVariable Long userId) {
        return dashboardService.getRecentTransactions(userId);
    }

    @GetMapping("/dashboard/{userId}/pending-transactions")
    public List<FundTransferResponse> getPendingTransactions(@PathVariable Long userId) {
        return dashboardService.getPendingTransactions(userId);
    }

    @GetMapping("/dashboard/checker/{checkerId}/pending-approvals")
    public List<FundTransferResponse> getCheckerPendingApprovals(@PathVariable Long checkerId) {
        return dashboardService.getPendingApprovalsForChecker(checkerId);
    }
}
