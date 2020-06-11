package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.TransactionReceipt;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void createOkWithLogs() {
        Address address = FactoryHelper.createRandomAddress();
        byte[] data = FactoryHelper.createRandomBytes(42);
        List<DataWord> topics = new ArrayList<>();
        DataWord topic1 = DataWord.fromUnsignedInteger(1);
        DataWord topic2 = DataWord.fromUnsignedInteger(4);
        DataWord topic3 = DataWord.fromUnsignedInteger(9);

        topics.add(topic1);
        topics.add(topic2);
        topics.add(topic3);

        Log log = new Log(address, data, topics);

        List<Log> logs = new ArrayList<>();
        logs.add(log);

        ExecutionResult executionResult = ExecutionResult.OkWithoutData(1000, logs);

        Assert.assertEquals(1000, executionResult.getGasUsed());
        Assert.assertFalse(executionResult.getLogs().isEmpty());
        Assert.assertEquals(1, executionResult.getLogs().size());
        Assert.assertSame(log, executionResult.getLogs().get(0));
        Assert.assertTrue(executionResult.wasSuccesful());

        TransactionReceipt transactionReceipt = executionResult.toTransactionReceipt();

        Assert.assertTrue(transactionReceipt.getSuccess());
        Assert.assertFalse(transactionReceipt.getLogs().isEmpty());
        Assert.assertEquals(1, transactionReceipt.getLogs().size());
        Assert.assertSame(log, transactionReceipt.getLogs().get(0));
        Assert.assertEquals(1000, transactionReceipt.getGasUsed());
    }
}
