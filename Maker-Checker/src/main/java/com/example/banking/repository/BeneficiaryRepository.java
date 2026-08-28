package com.example.banking.repository;

import com.example.banking.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {

    List<Beneficiary> findAllByOrderByCreatedAtDesc();

    List<Beneficiary> findByCreatedBy(Long userId);
}
