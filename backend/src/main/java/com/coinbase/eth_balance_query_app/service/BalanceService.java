package com.coinbase.eth_balance_query_app.service;

import com.coinbase.eth_balance_query_app.entities.BalanceRecord;
import com.coinbase.eth_balance_query_app.repository.BalanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BalanceService {

    private static final Logger logger = LoggerFactory.getLogger(BalanceService.class);
    private final BalanceRepository balanceRepository;

    public BalanceService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    public List<BalanceRecord> getBalances(String address, LocalDateTime start, LocalDateTime end) {
        String formattedAddress = address.strip();
        logger.info("Getting balances for address {}", formattedAddress);
        return balanceRepository.findByAddressAndTimestampBetween(formattedAddress, start, end);
    }
}
