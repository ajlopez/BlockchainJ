package com.ajlopez.blockchain.core;

import java.math.BigInteger;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class Transaction {
    private Address sender;
    private Address receiver;
    private BigInteger value;

    public Transaction(Address sender, Address receiver, BigInteger value) {
        this.sender = sender;
        this.receiver = receiver;
        this.value = value;
    }

    public Address getSender() { return this.sender; }

    public Address getReceiver() { return this.receiver; }

    public BigInteger getValue() { return this.value; }
}
