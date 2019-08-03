package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.encoding.TransactionEncoder;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HashUtils;

import java.math.BigInteger;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class Transaction {
    private final Address sender;
    private final Address receiver;
    private final BigInteger value;
    private final long nonce;
    private final byte[] data;
    private final long gas;
    private final BigInteger gasPrice;

    private Hash hash;

    public Transaction(Address sender, Address receiver, BigInteger value, long nonce, byte[] data, long gas, BigInteger gasPrice) {
        if (sender == null)
            throw new IllegalStateException("No sender in transaction");

        if (receiver == null)
            throw new IllegalStateException("No receiver in transaction");

        if (value == null)
            value = BigInteger.ZERO;

        if (gasPrice == null)
            gasPrice = BigInteger.ZERO;

        if (BigInteger.ZERO.compareTo(value) > 0)
            throw new IllegalStateException("Negative value in transaction");

        if (nonce < 0)
            throw new IllegalStateException("Negative nonce in transaction");

        this.sender = sender;
        this.receiver = receiver;
        this.value = value;
        this.nonce = nonce;
        this.data = ByteUtils.normalizeBytesToNull(data);
        this.gas = gas;
        this.gasPrice = gasPrice;
    }

    public Address getSender() { return this.sender; }

    public Address getReceiver() { return this.receiver; }

    public BigInteger getValue() { return this.value; }

    public long getNonce() { return this.nonce; }

    public byte[] getData() { return this.data; }

    public long getGas() { return this.gas; }

    public BigInteger getGasPrice() { return this.gasPrice; }

    public Hash getHash() {
        if (this.hash == null)
            this.hash = this.calculateHash();

        return this.hash;
    }

    private Hash calculateHash() {
        return HashUtils.calculateHash(TransactionEncoder.encode(this));
    }
}
