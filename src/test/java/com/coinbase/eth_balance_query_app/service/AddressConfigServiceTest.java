package com.coinbase.eth_balance_query_app.service;

import com.coinbase.eth_balance_query_app.entities.AddressConfig;
import com.coinbase.eth_balance_query_app.event.AddressChangedEvent;
import com.coinbase.eth_balance_query_app.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class AddressConfigServiceTest {

    private static final String TEST_ADDRESS = "0x0000000000000000000000000000000000000000";

    @InjectMocks
    private AddressConfigService addressConfigService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Transactional
    public void testSetCurrentAddress_NewAddress() {
        when(addressRepository.setCurrentAddress(TEST_ADDRESS)).thenReturn(0);

        addressConfigService.setCurrentAddress(TEST_ADDRESS);

        verify(addressRepository, times(1)).unsetCurrentAddress();
        verify(addressRepository, times(1)).setCurrentAddress(TEST_ADDRESS);
        verify(addressRepository, times(1)).save(any(AddressConfig.class));
        verify(eventPublisher, times(1)).publishEvent(any(AddressChangedEvent.class));
    }

    @Test
    @Transactional
    public void testSetCurrentAddressExistingAddress() {
        when(addressRepository.setCurrentAddress(TEST_ADDRESS)).thenReturn(1);

        addressConfigService.setCurrentAddress(TEST_ADDRESS);

        verify(addressRepository, times(1)).unsetCurrentAddress();
        verify(addressRepository, times(1)).setCurrentAddress(TEST_ADDRESS);
        verify(addressRepository, never()).save(any(AddressConfig.class));
        verify(eventPublisher, times(1)).publishEvent(any(AddressChangedEvent.class));
    }

    @Test
    public void testGetCurrentAddress() {
        when(addressRepository.findCurrentAddress())
                .thenReturn(java.util.Optional.of(new AddressConfig(1L, TEST_ADDRESS, true, LocalDateTime.now())));

        String actualAddress = addressConfigService.getCurrentAddress();
        assertEquals(TEST_ADDRESS, actualAddress);
    }

    @Test
    public void testGetCurrentAddressNotFound() {
        when(addressRepository.findCurrentAddress()).thenReturn(java.util.Optional.empty());

        String actualAddress = addressConfigService.getCurrentAddress();
        assertNull(actualAddress);
    }

    @Test
    public void testAddressExists() {
        String address = "0x1234567890abcdef1234567890abcdef12345678";
        when(addressRepository.existsByAddress(address)).thenReturn(true);

        boolean exists = addressConfigService.addressExists(address);
        assertTrue(exists);
    }
}
