package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.TransactionReceipt;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 10/06/2020.
 */
public class ExecutionResultTest {
    @Test
    public void createOkWithoutLogs() {
        ExecutionResult executionResult = ExecutionResult.OkWithoutData(1000, null);

        Assert.assertEquals(1000, executionResult.getGasUsed());
        Assert.assertTrue(executionResult.getLogs().isEmpty());
        Assert.assertTrue(executionResult.wasSuccesful());

        TransactionReceipt transactionReceipt = executionResult.toTransactionReceipt();

        Assert.assertTrue(transactionReceipt.getSuccess());
        Assert.assertTrue(transactionReceipt.getLogs().isEmpty());
        Assert.assertEquals(1000, transactionReceipt.getGasUsed());
    }
}
