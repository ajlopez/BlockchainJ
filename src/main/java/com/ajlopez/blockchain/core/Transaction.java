package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.encoding.BlockHeaderEncoder;
import com.ajlopez.blockchain.encoding.TransactionEncoder;
import com.ajlopez.blockchain.utils.HashUtils;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class Transaction {
    private Address sender;
    private Address receiver;
    private BigInteger value;
    private Hash hash;

    public Transaction(Address sender, Address receiver, BigInteger value) {
        this.sender = sender;
        this.receiver = receiver;
        this.value = value;
    }

    public Address getSender() { return this.sender; }

    public Address getReceiver() { return this.receiver; }

    public BigInteger getValue() { return this.value; }

    public Hash getHash() {
        if (this.hash == null)
            this.hash = this.calculateHash();

        return this.hash;
    }

    private Hash calculateHash() {
        try {
            return new Hash(HashUtils.sha3(TransactionEncoder.encode(this)));
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
}
