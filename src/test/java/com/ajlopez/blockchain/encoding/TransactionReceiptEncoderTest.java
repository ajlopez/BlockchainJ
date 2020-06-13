package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.TransactionReceipt;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.vms.eth.Log;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 26/05/2020.
 */
public class TransactionReceiptEncoderTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void encodeDecodeTransactionReceipt() {
        TransactionReceipt transactionReceipt = new TransactionReceipt(42, true, null);

        byte[] encoded = TransactionReceiptEncoder.encode(transactionReceipt);

        Assert.assertNotNull(encoded);

        TransactionReceipt result = TransactionReceiptEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(42, result.getGasUsed());
        Assert.assertTrue(result.getSuccess());
        Assert.assertTrue(result.getLogs().isEmpty());
    }

    @Test
    public void encodeDecodeTransactionReceiptWithLogs() {
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

        byte[] encoded = TransactionReceiptEncoder.encode(transactionReceipt);

        Assert.assertNotNull(encoded);

        TransactionReceipt result = TransactionReceiptEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(42, result.getGasUsed());
        Assert.assertTrue(result.getSuccess());

        Assert.assertFalse(result.getLogs().isEmpty());
        Assert.assertEquals(1, result.getLogs().size());

        Log resultlog = result.getLogs().get(0);

        Assert.assertNotNull(resultlog);
        Assert.assertEquals(log.getAddress(), resultlog.getAddress());
        Assert.assertArrayEquals(log.getData(), resultlog.getData());
        Assert.assertEquals(log.getTopics().size(), resultlog.getTopics().size());
        Assert.assertEquals(topic1, resultlog.getTopics().get(0));
        Assert.assertEquals(topic2, resultlog.getTopics().get(1));
        Assert.assertEquals(topic3, resultlog.getTopics().get(2));
    }

    @Test
    public void decodeInvalidEncodedTransactionReceipt() {
        byte[] encoded = RLP.encodeList(RLP.encode(new byte[1]), RLP.encode(new byte[2]), RLP.encode(new byte[3]), RLP.encode(new byte[4]));

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid transaction receipt encoding");
        TransactionReceiptEncoder.decode(encoded);
    }
}
