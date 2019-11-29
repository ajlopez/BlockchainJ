package com.ajlopez.blockchain.vms.eth;

import java.util.List;

/**
 * Created by ajlopez on 24/11/2019.
 */
public class ExecutionResult {
    private final long gasUsed;
    private final byte[] returnedData;
    private final List<Log> logs;
    private final boolean success;

    public static ExecutionResult OkWithoutData(long gasUsed, List<Log> logs) { return new ExecutionResult(gasUsed, null, logs, true); }

    public static ExecutionResult OkWithData(long gasUsed, byte[] returnedData, List<Log> logs) { return new ExecutionResult(gasUsed, returnedData, logs, true); }

    public static ExecutionResult ErrorReverted(long gasUsed, byte[] returnedData) { return new ExecutionResult(gasUsed, returnedData, null, false); }

    private ExecutionResult(long gasUsed, byte[] returnedData, List<Log> logs, boolean success) {
        this.gasUsed = gasUsed;
        this.returnedData = returnedData;
        this.logs = logs;
        this.success = success;
    }

    public boolean wasSuccesful() { return this.success; }

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
