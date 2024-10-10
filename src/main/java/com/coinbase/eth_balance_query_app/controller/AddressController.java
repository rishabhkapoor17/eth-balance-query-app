package com.coinbase.eth_balance_query_app.controller;

import com.coinbase.eth_balance_query_app.service.AddressConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    private final AddressConfigService addressConfigService;

    @Autowired
    public AddressController(AddressConfigService addressConfigService) {
        this.addressConfigService = addressConfigService;
    }

    @PostMapping("/current")
    public ResponseEntity<String> setCurrentAddress(@RequestBody String address) {
        addressConfigService.setCurrentAddress(address);
        return ResponseEntity.ok("Current address set successfully");
    }

    @GetMapping("/current")
    public ResponseEntity<String> getCurrentAddress() {
        String address = addressConfigService.getCurrentAddress();
        if (address != null) {
            return ResponseEntity.ok(address);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exists/{address}")
    public ResponseEntity<Boolean> addressExists(@PathVariable String address) {
        boolean exists = addressConfigService.addressExists(address);
        return ResponseEntity.ok(exists);
    }
}
