package com.ajlopez.blockchain.core;

/**
 * Created by ajlopez on 25/05/2020.
 */
public class TransactionReceipt {
    private final long gasUsed;
    private final boolean success;

    public TransactionReceipt(long gasUsed, boolean success) {
        this.gasUsed = gasUsed;
        this.success = success;
    }

    public long getGasUsed() {
        return this.gasUsed;
    }

    public boolean getSuccess() {
        return this.success;
    }
}
