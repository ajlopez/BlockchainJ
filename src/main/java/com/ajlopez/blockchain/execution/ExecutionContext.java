package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;

import java.math.BigInteger;

/**
 * Created by ajlopez on 16/02/2018.
 */
public interface ExecutionContext {
    void transfer(Address senderAddress, Address receiverAddress, BigInteger amount);

    void incrementNonce(Address address);

    BigInteger getBalance(Address address);

    long getNonce(Address address);

    void commit();

    void rollback();

    AccountState getAccountState(Address address);

    void putAccountState(Address address, AccountState accountState);
}
