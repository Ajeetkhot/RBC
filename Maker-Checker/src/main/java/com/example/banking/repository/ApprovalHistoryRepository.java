package com.example.banking.repository;

import com.example.banking.entity.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {

    List<ApprovalHistory> findByCheckerIdOrderByCreatedAtDesc(Long checkerId);
}
