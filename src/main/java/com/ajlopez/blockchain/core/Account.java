package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.Hash;

import java.math.BigInteger;

/**
 * Created by ajlopez on 09/11/2017.
 */
public class Account {
    private final BigInteger balance;
    private final long nonce;
    private final Hash codeHash;
    private final Hash storageHash;

    public Account() {
        this(BigInteger.ZERO, 0, null, null);
    }

    public Account(BigInteger balance, long nonce, Hash codeHash, Hash storageHash) {
        if (balance == null)
            balance = BigInteger.ZERO;

        if (BigInteger.ZERO.compareTo(balance) > 0)
            throw new IllegalStateException("Negative balance in account state");

        if (nonce < 0)
            throw new IllegalStateException("Negative nonce in account state");

        this.balance = balance;
        this.nonce = nonce;
        this.codeHash = codeHash;
        this.storageHash = storageHash;
    }

    public BigInteger getBalance() {
        return this.balance;
    }

    public long getNonce() { return this.nonce; }

    public Hash getCodeHash() { return this.codeHash; }

    public Hash getStorageHash() { return this.storageHash; }
}
