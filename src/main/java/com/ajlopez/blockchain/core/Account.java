package com.ajlopez.blockchain.core;

import java.math.BigInteger;

/**
 * Created by ajlopez on 09/11/2017.
 */
public class Account {
    private final BigInteger balance;
    private final long nonce;

    public Account() {
        this(BigInteger.ZERO, 0);
    }

    public Account(BigInteger balance, long nonce) {
        if (balance == null)
            balance = BigInteger.ZERO;

        if (BigInteger.ZERO.compareTo(balance) > 0)
            throw new IllegalStateException("Negative balance in account state");

        if (nonce < 0)
            throw new IllegalStateException("Negative nonce in account state");

        this.balance = balance;
        this.nonce = nonce;
    }

    public BigInteger getBalance() {
        return this.balance;
    }

    public long getNonce() { return this.nonce; }
}
