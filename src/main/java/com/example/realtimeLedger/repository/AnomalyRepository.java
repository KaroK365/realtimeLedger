package com.example.realtimeLedger.repository;

import com.example.realtimeLedger.entity.Anomaly;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnomalyRepository extends JpaRepository<Anomaly, Long> {
}
