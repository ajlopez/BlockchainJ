package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.types.*;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.vms.eth.Log;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 06/06/2020.
 */
public class LogEncoderTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void encodeDecodeLog() {
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

        byte[] encoded = LogEncoder.encode(log);

        Assert.assertNotNull(encoded);

        Log result = LogEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(log.getAddress(), result.getAddress());
        Assert.assertArrayEquals(log.getData(), result.getData());
        Assert.assertEquals(log.getTopics().size(), result.getTopics().size());

        for (int k = 0; k < log.getTopics().size(); k++)
            Assert.assertEquals(log.getTopics().get(k), result.getTopics().get(k));
    }

    @Test
    public void encodeDecodeWithoutData() {
        Address address = FactoryHelper.createRandomAddress();
        List<DataWord> topics = new ArrayList<>();
        DataWord topic1 = DataWord.fromUnsignedInteger(1);
        DataWord topic2 = DataWord.fromUnsignedInteger(4);
        DataWord topic3 = DataWord.fromUnsignedInteger(9);

        topics.add(topic1);
        topics.add(topic2);
        topics.add(topic3);

        Log log = new Log(address, null, topics);

        byte[] encoded = LogEncoder.encode(log);

        Assert.assertNotNull(encoded);

        Log result = LogEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(log.getAddress(), result.getAddress());
        Assert.assertNull(result.getData());
        Assert.assertEquals(log.getTopics().size(), result.getTopics().size());

        for (int k = 0; k < log.getTopics().size(); k++)
            Assert.assertEquals(log.getTopics().get(k), result.getTopics().get(k));
    }

    @Test
    public void decodeInvalidEncodedLog() {
        byte[] rlpData1 = RLP.encode(FactoryHelper.createRandomBytes(10));
        byte[] rlpData2 = RLP.encode(FactoryHelper.createRandomBytes(20));
        byte[] encoded = RLP.encodeList(rlpData1, rlpData2);

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid log encoding");
        LogEncoder.decode(encoded);
    }
}
