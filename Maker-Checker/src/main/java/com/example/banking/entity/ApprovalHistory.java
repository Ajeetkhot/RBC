package com.example.banking.entity;

import com.example.banking.enums.ApprovalAction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private Long checkerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalAction action;

    @Column(length = 500)
    private String remarks;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
