package com.coinbase.eth_balance_query_app.service;

import com.coinbase.eth_balance_query_app.client.EthereumRpcClient;
import com.coinbase.eth_balance_query_app.entities.BalanceRecord;
import com.coinbase.eth_balance_query_app.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

public class BalanceUpdateServiceTest {

    private static final String TEST_ADDRESS = "0x0000000000000000000000000000000000000000";

    @InjectMocks
    private BalanceUpdateService balanceUpdateService;
    @Mock
    private BalanceRepository balanceRepository;
    @Mock
    private AddressConfigService addressConfigService;
    @Mock
    private EthereumRpcClient ethereumRpcClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateBalancesSuccess() {
        when(addressConfigService.getCurrentAddress()).thenReturn(TEST_ADDRESS);
        when(ethereumRpcClient.getBalance(TEST_ADDRESS)).thenReturn(BigDecimal.valueOf(100));

        balanceUpdateService.updateBalances();

        ArgumentCaptor<BalanceRecord> captor = forClass(BalanceRecord.class);
        verify(balanceRepository, times(1)).save(captor.capture());

        BalanceRecord capturedRecord = captor.getValue();

        assertEquals(TEST_ADDRESS, capturedRecord.getAddress());
        assertEquals(BigDecimal.valueOf(100), capturedRecord.getBalance());
        assertNotNull(capturedRecord.getTimestamp());
    }

    @Test
    public void testUpdateBalancesNoConfiguredAddress() {
        when(addressConfigService.getCurrentAddress()).thenReturn(null);
        verify(balanceRepository, never()).save(any(BalanceRecord.class));
    }
}
