package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Hash;

/**
 * Created by ajlopez on 09/11/2017.
 */
public class Account {
    private final Coin balance;
    private final long nonce;
    private final long codeLength;
    private final Hash codeHash;
    private final Hash storageHash;

    public Account() {
        this(Coin.ZERO, 0, 0, null, null);
    }

    public Account(Coin balance, long nonce, long codeLength, Hash codeHash, Hash storageHash) {
        if (balance == null)
            balance = Coin.ZERO;

        if (nonce < 0)
            throw new IllegalStateException("Negative nonce in account state");

        this.balance = balance;
        this.nonce = nonce;
        this.codeLength = codeLength;
        this.codeHash = codeHash;
        this.storageHash = storageHash;
    }

    public Coin getBalance() {
        return this.balance;
    }

    public long getNonce() { return this.nonce; }

    public long getCodeLength() { return this.codeLength; }

    public Hash getCodeHash() { return this.codeHash; }

    public Hash getStorageHash() { return this.storageHash; }
}
