package com.coinbase.eth_balance_query_app.repository;

import com.coinbase.eth_balance_query_app.entities.AddressConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<AddressConfig, Long> {
    @Query("SELECT a FROM AddressConfig a WHERE a.isCurrent = true")
    Optional<AddressConfig> findCurrentAddress();

    @Modifying
    @Query("UPDATE AddressConfig a SET a.isCurrent = false")
    void unsetCurrentAddress();

    @Modifying
    @Query("UPDATE AddressConfig a SET a.isCurrent = true WHERE a.address = :address")
    int setCurrentAddress(@Param("address") String address);

    boolean existsByAddress(String address);
}

