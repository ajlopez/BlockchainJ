package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.Account;

import java.math.BigInteger;

/**
 * Created by ajlopez on 26/11/2018.
 */
public class AccountState {
    private BigInteger balance;
    private long nonce;

    public static AccountState fromAccount(Account account) {
        return new AccountState(account.getBalance(), account.getNonce());
    }

    public AccountState() {
        this(BigInteger.ZERO, 0);
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

    public Account toAccount() {
        return new Account(this.balance, this.nonce);
    }
}
