package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.*;
import com.ajlopez.blockchain.encoding.TransactionEncoder;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HashUtils;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class Transaction {
    private final Address sender;
    private final Address receiver;
    private final Coin value;
    private final long nonce;
    private final byte[] data;
    private final long gas;
    private final Coin gasPrice;

    private TransactionHash hash;

    public Transaction(Address sender, Address receiver, Coin value, long nonce, byte[] data, long gas, Coin gasPrice) {
        if (sender == null)
            throw new IllegalStateException("No sender in transaction");

        if (value == null)
            value = Coin.ZERO;

        if (gasPrice == null)
            gasPrice = Coin.ZERO;

        if (nonce < 0)
            throw new IllegalStateException("Negative nonce in transaction");

        this.sender = sender;
        this.receiver = Address.normalizeToNull(receiver);
        this.value = value;
        this.nonce = nonce;
        this.data = ByteUtils.normalizeBytesToNull(data);
        this.gas = gas;
        this.gasPrice = gasPrice;
    }

    public Address getSender() { return this.sender; }

    public Address getReceiver() { return this.receiver; }

    public Coin getValue() { return this.value; }

    public long getNonce() { return this.nonce; }

    public byte[] getData() { return this.data; }

    public long getGas() { return this.gas; }

    public Coin getGasPrice() { return this.gasPrice; }

    public TransactionHash getHash() {
        if (this.hash == null)
            this.hash = this.calculateHash();

        return this.hash;
    }

    public Transaction withNonce(long newnonce) {
        return new Transaction(this.sender, this.receiver, this.value, newnonce, this.data, this.gas, this.gasPrice);
    }

    private TransactionHash calculateHash() {
        return new TransactionHash(HashUtils.keccak256(TransactionEncoder.encode(this)));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (this == obj)
            return true;

        if (!(obj instanceof Transaction))
            return false;

        return this.getHash().equals(((Transaction)obj).getHash());
    }

    @Override
    public int hashCode() {
        return this.getHash().hashCode();
    }
}
