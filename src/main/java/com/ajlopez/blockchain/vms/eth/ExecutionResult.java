package com.ajlopez.blockchain.vms.eth;

import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 24/11/2019.
 */
public class ExecutionResult {
    private long gasUsed;
    private final byte[] returnedData;
    private final List<Log> logs;
    private final boolean success;
    private final Exception exception;

    public static ExecutionResult OkWithoutData(long gasUsed, List<Log> logs) { return new ExecutionResult(gasUsed, null, logs, true, null); }

    public static ExecutionResult OkWithData(long gasUsed, byte[] returnedData, List<Log> logs) { return new ExecutionResult(gasUsed, returnedData, logs, true, null); }

    public static ExecutionResult ErrorReverted(long gasUsed, byte[] returnedData) { return new ExecutionResult(gasUsed, returnedData, null, false, null); }

    public static ExecutionResult ErrorException(long gasUsed, Exception exception) { return new ExecutionResult(gasUsed, null, null, false, exception); }

    private ExecutionResult(long gasUsed, byte[] returnedData, List<Log> logs, boolean success, Exception exception) {
        this.gasUsed = gasUsed;
        this.returnedData = returnedData;

        if (logs == null)
            this.logs = Collections.emptyList();
        else
            this.logs = Collections.unmodifiableList(logs);

        this.success = success;
        this.exception = exception;
    }

    public boolean wasSuccesful() { return this.success; }

    public long getGasUsed() {
        return this.gasUsed;
    }

    public void addGasUsed(long gas) { this.gasUsed += gas; }

    // TODO return copy?
    public byte[] getReturnedData() {
        return this.returnedData;
    }

    public List<Log> getLogs() {
        return this.logs;
    }

    public Exception getException() {
        return this.exception;
    }
}
