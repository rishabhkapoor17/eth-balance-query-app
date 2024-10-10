package com.coinbase.eth_balance_query_app.service;

import com.coinbase.eth_balance_query_app.entities.AddressConfig;
import com.coinbase.eth_balance_query_app.event.AddressChangedEvent;
import com.coinbase.eth_balance_query_app.repository.AddressRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class AddressConfigService {

    private static final Logger logger = LoggerFactory.getLogger(AddressConfigService.class);
    private final AddressRepository addressRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AddressConfigService(AddressRepository addressRepository, ApplicationEventPublisher eventPublisher) {
        this.addressRepository = addressRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void setCurrentAddress(String address) {
        logger.info("Setting new current address: " + address);
        String formattedAddress = address.strip();
        addressRepository.unsetCurrentAddress();
        int updated = addressRepository.setCurrentAddress(formattedAddress);
        if (updated == 0) {
            // Address doesn't exist, create new entry
            AddressConfig newConfig = new AddressConfig();
            newConfig.setAddress(formattedAddress);
            newConfig.setCurrent(true);
            addressRepository.save(newConfig);
        }
        logger.info("Finished setting new current address: " + formattedAddress);

        eventPublisher.publishEvent(new AddressChangedEvent(this, formattedAddress));
    }

    public String getCurrentAddress() {
        logger.info("Getting current address");
        return addressRepository.findCurrentAddress()
                .map(AddressConfig::getAddress)
                .orElse(null);
    }

    public boolean addressExists(String address) {
        String formattedAddress = address.strip();
        return addressRepository.existsByAddress(formattedAddress);
    }
}
