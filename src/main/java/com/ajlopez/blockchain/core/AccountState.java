package com.ajlopez.blockchain.core;

import java.math.BigInteger;

/**
 * Created by ajlopez on 09/11/2017.
 */
public class AccountState {
    private BigInteger balance;

    public AccountState() {
        this.balance = BigInteger.ZERO;
    }

    public BigInteger getBalance() {
        return this.balance;
    }

    public void addToBalance(BigInteger amount) {
        this.balance = this.balance.add(amount);
    }
}
