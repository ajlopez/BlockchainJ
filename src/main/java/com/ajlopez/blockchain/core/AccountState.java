package com.ajlopez.blockchain.core;

import java.math.BigInteger;

/**
 * Created by ajlopez on 09/11/2017.
 */
public class AccountState {
    private BigInteger balance;
    private long nonce;

    public AccountState() {
        this.balance = BigInteger.ZERO;
    }

    public AccountState(BigInteger balance, long nonce) {
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

    public void incrementNonce() {
        this.nonce++;
    }

    public void addToBalance(BigInteger amount) {
        BigInteger newbalance = this.balance.add(amount);

        if (newbalance.compareTo(BigInteger.ZERO) < 0)
            throw new IllegalStateException("Invalid balance");

        this.balance = newbalance;
    }

    public void subtractFromBalance(BigInteger amount) {
        BigInteger newbalance = this.balance.subtract(amount);

        if (newbalance.compareTo(BigInteger.ZERO) < 0)
            throw new IllegalStateException("Invalid balance");

        this.balance = newbalance;
    }
}
