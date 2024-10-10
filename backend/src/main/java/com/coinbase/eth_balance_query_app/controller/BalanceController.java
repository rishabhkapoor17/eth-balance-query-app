package com.coinbase.eth_balance_query_app.controller;

import com.coinbase.eth_balance_query_app.entities.BalanceRecord;
import com.coinbase.eth_balance_query_app.service.BalanceService;
import com.coinbase.eth_balance_query_app.service.BalanceUpdateService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BalanceController {

    private static final long ONE_SECOND = 1000;
    private static final long ONE_DAY = 24 * 60 * 60 * 1000; // 86400000

    private final BalanceService balanceService;
    private final BalanceUpdateService balanceUpdateService;

    public BalanceController(BalanceService balanceService, BalanceUpdateService balanceUpdateService) {
        this.balanceService = balanceService;
        this.balanceUpdateService = balanceUpdateService;
    }

    @GetMapping("/balances")
    public ResponseEntity<List<BalanceRecord>> getBalances(
            @RequestParam String address,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<BalanceRecord> balances = balanceService.getBalances(address, start, end);
        return ResponseEntity.ok(balances);
    }

    @PostMapping("/update-interval")
    public ResponseEntity<String> updateInterval(@RequestParam long intervalMs) {
        if (intervalMs < ONE_SECOND|| intervalMs > ONE_DAY) {
            return ResponseEntity.badRequest().body(
                    String.format("Interval must be between %d ms (1 second) and %d ms (1 day)",
                            ONE_SECOND, ONE_DAY)
            );
        }
        balanceUpdateService.setUpdateInterval(intervalMs);
        return ResponseEntity.ok("Update interval set to " + intervalMs + " ms");
    }
}
