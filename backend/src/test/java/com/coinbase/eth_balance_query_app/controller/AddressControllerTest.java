package com.coinbase.eth_balance_query_app.controller;

import com.coinbase.eth_balance_query_app.service.AddressConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AddressControllerTest {

    private static final String TEST_ADDRESS = "0x0000000000000000000000000000000000000000";

    @InjectMocks
    private AddressController addressController;

    @Mock
    private AddressConfigService addressConfigService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSetCurrentAddress() {
        ResponseEntity<String> response = addressController.setCurrentAddress(TEST_ADDRESS);

        verify(addressConfigService, times(1)).setCurrentAddress(TEST_ADDRESS);
        assertEquals(ResponseEntity.ok("Current address set successfully"), response);
    }

    @Test
    public void testGetCurrentAddress() {
        when(addressConfigService.getCurrentAddress()).thenReturn(TEST_ADDRESS);

        ResponseEntity<String> response = addressController.getCurrentAddress();
        assertEquals(ResponseEntity.ok(TEST_ADDRESS), response);
    }

    @Test
    public void testGetCurrentAddressNotFound() {
        when(addressConfigService.getCurrentAddress()).thenReturn(null);

        ResponseEntity<String> response = addressController.getCurrentAddress();
        assertEquals(ResponseEntity.notFound().build(), response);
    }

    @Test
    public void testAddressExists() {
        when(addressConfigService.addressExists(TEST_ADDRESS)).thenReturn(true);

        ResponseEntity<Boolean> response = addressController.addressExists(TEST_ADDRESS);
        assertEquals(ResponseEntity.ok(true), response);
    }
}

