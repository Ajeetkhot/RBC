package com.example.banking.controller;

import com.example.banking.dto.BeneficiaryRequest;
import com.example.banking.dto.BeneficiaryResponse;
import com.example.banking.service.BeneficiaryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    @PostMapping("/beneficiaries")
    @ResponseStatus(HttpStatus.CREATED)
    public BeneficiaryResponse createBeneficiary(@Valid @RequestBody BeneficiaryRequest request) {
        return beneficiaryService.createBeneficiary(request);
    }

    @GetMapping("/beneficiaries")
    public List<BeneficiaryResponse> getAllBeneficiaries() {
        return beneficiaryService.getAllBeneficiaries();
    }

    @GetMapping("/beneficiaries/user/{userId}")
    public List<BeneficiaryResponse> getBeneficiariesForUser(@PathVariable Long userId) {
        return beneficiaryService.getBeneficiariesForUser(userId);
    }

    @GetMapping("/beneficiaries/{beneficiaryId}")
    public BeneficiaryResponse getBeneficiary(@PathVariable Long beneficiaryId) {
        return beneficiaryService.getBeneficiary(beneficiaryId);
    }

    @PutMapping("/beneficiaries/{beneficiaryId}")
    public BeneficiaryResponse updateBeneficiary(@PathVariable Long beneficiaryId, @Valid @RequestBody BeneficiaryRequest request) {
        return beneficiaryService.updateBeneficiary(beneficiaryId, request);
    }

    @PutMapping("/beneficiaries/{beneficiaryId}/activate")
    public BeneficiaryResponse activateBeneficiary(@PathVariable Long beneficiaryId) {
        return beneficiaryService.activateBeneficiary(beneficiaryId);
    }

    @PutMapping("/beneficiaries/{beneficiaryId}/deactivate")
    public BeneficiaryResponse deactivateBeneficiary(@PathVariable Long beneficiaryId) {
        return beneficiaryService.deactivateBeneficiary(beneficiaryId);
    }
}
