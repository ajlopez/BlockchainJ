package com.ajlopez.blockchain.vms.eth;

import java.util.List;

/**
 * Created by ajlopez on 24/11/2019.
 */
public class ExecutionResult {
    private final long gasUsed;
    private final byte[] returnedData;
    private final List<Log> logs;

    public static ExecutionResult OkWithoutData(long gasUsed, List<Log> logs) { return new ExecutionResult(gasUsed, null, logs); }

    public static ExecutionResult OkWithData(long gasUsed, byte[] returnedData, List<Log> logs) { return new ExecutionResult(gasUsed, returnedData, logs); }

    private ExecutionResult(long gasUsed, byte[] returnedData, List<Log> logs) {
        this.gasUsed = gasUsed;
        this.returnedData = returnedData;
        this.logs = logs;
    }

    public long getGasUsed() {
        return this.gasUsed;
    }

    // TODO return copy?
    public byte[] getReturnedData() {
        return this.returnedData;
    }

    // TODO return inmutable? or in constructor?
    public List<Log> getLogs() {
        return this.logs;
    }
}
