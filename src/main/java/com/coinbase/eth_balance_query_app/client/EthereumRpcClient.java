package com.coinbase.eth_balance_query_app.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import java.math.BigDecimal;
import java.math.BigInteger;

@Component
public class EthereumRpcClient {
    private static final Logger logger = LoggerFactory.getLogger(EthereumRpcClient.class);

    private final Web3j web3j;

    public EthereumRpcClient(@Value("${ethereum.rpc.url}") String rpcUrl) {
        this.web3j = Web3j.build(new HttpService(rpcUrl));
    }

    public BigDecimal getBalance(String address) {
        try {
            String formattedAddress = address.strip();
            logger.info("Fetching balance for address: " + formattedAddress);
            EthGetBalance ethGetBalance = web3j.ethGetBalance(formattedAddress, DefaultBlockParameterName.LATEST).send();
            if (ethGetBalance.hasError()) {
                throw new RuntimeException(String.format("Error fetching balance for address %s: %s",
                        formattedAddress, ethGetBalance.getError().getMessage()));
            }
            BigInteger balanceWei = ethGetBalance.getBalance();
            BigDecimal balanceEth = Convert.fromWei(new BigDecimal(balanceWei), Convert.Unit.ETHER);

            logger.info("Fetched balance for " + formattedAddress + ": " + balanceEth + " ETH");

            return balanceEth;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching balance for address " + address, e);
        }
    }
}