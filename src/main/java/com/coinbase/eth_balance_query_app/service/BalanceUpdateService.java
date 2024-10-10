package com.coinbase.eth_balance_query_app.service;

import com.coinbase.eth_balance_query_app.client.EthereumRpcClient;
import com.coinbase.eth_balance_query_app.entities.BalanceRecord;
import com.coinbase.eth_balance_query_app.event.AddressChangedEvent;
import com.coinbase.eth_balance_query_app.repository.BalanceRepository;
import jakarta.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

@Service
public class BalanceUpdateService implements SchedulingConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(BalanceUpdateService.class);
    private final BalanceRepository balanceRepository;
    private final AddressConfigService addressConfigService;
    private final EthereumRpcClient ethereumRpcClient;

    private long updateIntervalMs = 60000; // Default to 1 minute
    private ScheduledTaskRegistrar taskRegistrar;
    private ScheduledFuture<?> scheduledTask;

    public BalanceUpdateService(BalanceRepository balanceRepository,
                                AddressConfigService addressConfigService,
                                EthereumRpcClient ethereumRpcClient) {
        this.balanceRepository = balanceRepository;
        this.addressConfigService = addressConfigService;
        this.ethereumRpcClient = ethereumRpcClient;
    }

    @Nullable
    @Override
    public void configureTasks(@NotNull ScheduledTaskRegistrar taskRegistrar) {
        this.taskRegistrar = taskRegistrar;

        logger.info("Performing initial balance update on application start");
        scheduleBalanceUpdate();
    }

    private void scheduleBalanceUpdate() {
        logger.info("Configuring balance update task with interval of {} ms", updateIntervalMs);

        // Cancel any already set tasks
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(false);
        }

        scheduledTask = Objects.requireNonNull(taskRegistrar.getScheduler()).scheduleWithFixedDelay(
                this::updateBalances,
                Duration.ofMillis(updateIntervalMs)
        );
    }

    public void setUpdateInterval(long intervalMs) {
        logger.info("Updating balance check interval to {} ms", intervalMs);
        this.updateIntervalMs = intervalMs;

        scheduleBalanceUpdate();

        updateBalances();
    }

    public void updateBalances() {
        logger.info("Starting balance update process");
        try {
            String address = addressConfigService.getCurrentAddress();
            if (address == null) {
                throw new RuntimeException("No address configured");
            }

            BigDecimal balance = ethereumRpcClient.getBalance(address);

            BalanceRecord record = new BalanceRecord();
            record.setAddress(address);
            record.setBalance(balance);
            record.setTimestamp(LocalDateTime.now());

            balanceRepository.save(record);
            logger.info("Balance updated successfully for address: {}", address);
            Instant nextExecutionTime = Instant.now().plusMillis(updateIntervalMs);
            logger.info("Next balance update scheduled for: {}", nextExecutionTime);

        } catch (Exception e) {
            logger.error("Error updating balance: ", e);
        }
    }

    @EventListener
    public void handleAddressChanged(AddressChangedEvent event) {
        logger.info("Received com.coinbase.eth_balance_query_app.event for changed address: {}", event.getNewAddress());
        // Trigger balance update when the address changes
        updateBalances();
    }
}



