package com.coinbase.eth_balance_query_app.repository;

import com.coinbase.eth_balance_query_app.entities.BalanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BalanceRepository extends JpaRepository<BalanceRecord, Long> {
    List<BalanceRecord> findByAddressAndTimestampBetween(String address, LocalDateTime start, LocalDateTime end);
}