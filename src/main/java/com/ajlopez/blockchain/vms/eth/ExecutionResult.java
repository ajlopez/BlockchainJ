package com.ajlopez.blockchain.vms.eth;

/**
 * Created by ajlopez on 24/11/2019.
 */
public class ExecutionResult {
    private final byte[] returnedData;

    public ExecutionResult(byte[] returnedData) {
        this.returnedData = returnedData;
    }

    public byte[] getReturnedData() {
        return this.returnedData;
    }
}
