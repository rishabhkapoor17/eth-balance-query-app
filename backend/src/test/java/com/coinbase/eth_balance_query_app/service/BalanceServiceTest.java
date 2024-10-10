package com.coinbase.eth_balance_query_app.service;

import com.coinbase.eth_balance_query_app.entities.BalanceRecord;
import com.coinbase.eth_balance_query_app.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BalanceServiceTest {

    private static final String TEST_ADDRESS = "0x0000000000000000000000000000000000000000";

    @InjectMocks
    private BalanceService balanceService;

    @Mock
    private BalanceRepository balanceRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetBalances() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        List<BalanceRecord> expectedBalances = Collections.singletonList(new BalanceRecord());
        when(balanceRepository.findByAddressAndTimestampBetween(TEST_ADDRESS, start, end)).thenReturn(expectedBalances);

        List<BalanceRecord> actualBalances = balanceService.getBalances(TEST_ADDRESS, start, end);
        assertEquals(expectedBalances, actualBalances);
    }
}
