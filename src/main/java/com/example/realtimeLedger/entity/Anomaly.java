package com.example.realtimeLedger.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "anomalies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Anomaly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "detected_at", insertable = false, updatable = false)
    private java.time.OffsetDateTime detectedAt;
}
