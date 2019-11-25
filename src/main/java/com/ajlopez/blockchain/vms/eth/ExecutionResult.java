package com.ajlopez.blockchain.vms.eth;

/**
 * Created by ajlopez on 24/11/2019.
 */
public class ExecutionResult {
    private final long gasUsed;
    private final byte[] returnedData;

    public ExecutionResult(long gasUsed, byte[] returnedData) {
        this.gasUsed = gasUsed;
        this.returnedData = returnedData;
    }

    public long getGasUsed() {
        return this.gasUsed;
    }

    public byte[] getReturnedData() {
        return this.returnedData;
    }
}
