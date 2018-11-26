package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.store.AccountStore;

import java.math.BigInteger;

/**
 * Created by ajlopez on 26/11/2018.
 */
public class ExecutionContext {
    private AccountStore accountStore;

    public ExecutionContext(AccountStore accountStore) {
        this.accountStore = accountStore;
    }

    public void transfer(Address from, Address to, BigInteger amount) {

    }

    public BigInteger getBalance(Address address) {
        return this.accountStore.getAccount(address).getBalance();
    }
}
