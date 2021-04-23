package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.types.Address;

/**
 * Created by ajlopez on 23/04/2021.
 */
public class MinerConfiguration {
    private final Address coinbase;
    private final long gasLimit;
    private final int noUncles;

    public MinerConfiguration(Address coinbase, long gasLimit, int noUncles) {
        this.coinbase = coinbase;
        this.gasLimit = gasLimit;
        this.noUncles = noUncles;
    }

    public Address getCoinbase() { return this.coinbase; }

    public long getGasLimit() { return this.gasLimit; }

    public int getNoUncles() { return this.noUncles; }
}
