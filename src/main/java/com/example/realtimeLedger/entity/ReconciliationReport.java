package com.example.realtimeLedger.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reconciliation_reports")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReconciliationReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ledger_sum")
    private Long ledgerSum;

    @Column(name = "accounts_sum")
    private Long accountsSum;

    @Column(name = "mismatch")
    private Boolean mismatch;

    @Column(name = "details")
    private String details;

    @Column(name = "run_at", insertable = false, updatable = false)
    private java.time.OffsetDateTime runAt;
}
