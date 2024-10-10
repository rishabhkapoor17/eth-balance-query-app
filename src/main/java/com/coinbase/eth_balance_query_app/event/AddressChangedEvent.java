package com.coinbase.eth_balance_query_app.event;

import org.springframework.context.ApplicationEvent;

public class AddressChangedEvent extends ApplicationEvent {
    private final String newAddress;

    public AddressChangedEvent(Object source, String newAddress) {
        super(source);
        this.newAddress = newAddress;
    }

    public String getNewAddress() {
        return newAddress;
    }
}