package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.vms.eth.Log;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 25/05/2020.
 */
public class TransactionReceiptTest {
    @Test
    public void simpleCreationTest() {
        TransactionReceipt transactionReceipt = new TransactionReceipt(42, true, null);

        Assert.assertEquals(42, transactionReceipt.getGasUsed());
        Assert.assertTrue(transactionReceipt.getSuccess());
        Assert.assertTrue(transactionReceipt.getLogs().isEmpty());
    }

    @Test
    public void createWithOneLog() {
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

        TransactionReceipt transactionReceipt = new TransactionReceipt(42, true, logs);

        Assert.assertEquals(42, transactionReceipt.getGasUsed());
        Assert.assertTrue(transactionReceipt.getSuccess());
        Assert.assertFalse(transactionReceipt.getLogs().isEmpty());
        Assert.assertEquals(1, transactionReceipt.getLogs().size());
        Assert.assertSame(log, transactionReceipt.getLogs().get(0));
    }

    @Test
    public void createWithUmmodifiableLogList() {
        Address address = FactoryHelper.createRandomAddress();
        byte[] data = FactoryHelper.createRandomBytes(42);
        byte[] data2 = FactoryHelper.createRandomBytes(42);
        List<DataWord> topics = new ArrayList<>();
        DataWord topic1 = DataWord.fromUnsignedInteger(1);
        DataWord topic2 = DataWord.fromUnsignedInteger(4);
        DataWord topic3 = DataWord.fromUnsignedInteger(9);

        topics.add(topic1);
        topics.add(topic2);
        topics.add(topic3);

        Log log = new Log(address, data, topics);
        Log log2 = new Log(address, data2, topics);

        List<Log> logs = new ArrayList<>();

        logs.add(log);

        TransactionReceipt transactionReceipt = new TransactionReceipt(42, true, logs);

        Assert.assertEquals(42, transactionReceipt.getGasUsed());
        Assert.assertTrue(transactionReceipt.getSuccess());
        Assert.assertFalse(transactionReceipt.getLogs().isEmpty());
        Assert.assertEquals(1, transactionReceipt.getLogs().size());
        Assert.assertSame(log, transactionReceipt.getLogs().get(0));

        logs.add(log2);

        Assert.assertEquals(1, transactionReceipt.getLogs().size());
        Assert.assertSame(log, transactionReceipt.getLogs().get(0));
    }
}
