package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.Address;

/**
 * Created by ajlopez on 21/12/2018.
 */
public class BlockData {
    private long number;
    private long timestamp;
    private Address coinbase;

    public BlockData(long number, long timestamp, Address coinbase) {
        this.number = number;
        this.timestamp = timestamp;
        this.coinbase = coinbase;
    }

    public long getNumber() { return this.number; }

    public long getTimestamp() { return this.timestamp; }

    public Address getCoinbase() { return this.coinbase; }
}
