package com.coinbase.eth_balance_query_app.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddressChangedEventTest {

    private static final String TEST_ADDRESS = "0x0000000000000000000000000000000000000000";

    @Test
    public void testAddressChangedEvent() {
        AddressChangedEvent event = new AddressChangedEvent(new Object(), TEST_ADDRESS);
        assertEquals(TEST_ADDRESS, event.getNewAddress());
    }
}
