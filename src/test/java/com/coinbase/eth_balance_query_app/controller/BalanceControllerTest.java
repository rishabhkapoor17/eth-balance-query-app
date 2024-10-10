package com.coinbase.eth_balance_query_app.controller;

import com.coinbase.eth_balance_query_app.entities.BalanceRecord;
import com.coinbase.eth_balance_query_app.service.BalanceService;
import com.coinbase.eth_balance_query_app.service.BalanceUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BalanceControllerTest {

    private static final String TEST_ADDRESS = "0x0000000000000000000000000000000000000000";

    @InjectMocks
    private BalanceController balanceController;

    @Mock
    private BalanceService balanceService;

    @Mock
    private BalanceUpdateService balanceUpdateService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetBalances() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        List<BalanceRecord> expectedBalances = Collections.singletonList(new BalanceRecord());
        when(balanceService.getBalances(TEST_ADDRESS, start, end)).thenReturn(expectedBalances);

        ResponseEntity<List<BalanceRecord>> response = balanceController.getBalances(TEST_ADDRESS, start, end);

        assertEquals(ResponseEntity.ok(expectedBalances), response);
    }

    @Test
    public void testUpdateIntervalValid() {
        long validInterval = 60000;

        ResponseEntity<String> response = balanceController.updateInterval(validInterval);

        verify(balanceUpdateService, times(1)).setUpdateInterval(validInterval);
        assertEquals(ResponseEntity.ok("Update interval set to " + validInterval + " ms"), response);
    }

    @Test
    public void testUpdateIntervalInvalid() {
        long invalidInterval = 500;

        ResponseEntity<String> response = balanceController.updateInterval(invalidInterval);

        assertEquals(ResponseEntity.badRequest().body("Interval must be between 1000 ms (1 second) and 86400000 ms (1 day)"), response);
        verify(balanceUpdateService, never()).setUpdateInterval(anyLong());
    }
}
