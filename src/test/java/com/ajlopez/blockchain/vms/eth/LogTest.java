package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.DataWord;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 28/01/2019.
 */
public class LogTest {
    @Test
    public void createLog() {
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

        Assert.assertEquals(address, log.getAddress());
        Assert.assertEquals(data, log.getData());
        Assert.assertEquals(topics, log.getTopics());
    }

    @Test
    public void createLogWithoutTopics() {
        Address address = FactoryHelper.createRandomAddress();
        byte[] data = FactoryHelper.createRandomBytes(42);

        Log log = new Log(address, data, null);

        Assert.assertEquals(address, log.getAddress());
        Assert.assertEquals(data, log.getData());
        Assert.assertTrue(log.getTopics().isEmpty());
    }

    @Test
    public void ummodifiableTopicsListInLog() {
        Address address = FactoryHelper.createRandomAddress();
        byte[] data = FactoryHelper.createRandomBytes(42);
        List<DataWord> topics = new ArrayList<>();
        DataWord topic1 = DataWord.fromUnsignedInteger(1);
        DataWord topic2 = DataWord.fromUnsignedInteger(4);
        DataWord topic3 = DataWord.fromUnsignedInteger(9);
        DataWord topic4 = DataWord.fromUnsignedInteger(10);

        topics.add(topic1);
        topics.add(topic2);
        topics.add(topic3);

        Log log = new Log(address, data, topics);

        topics.add(topic4);

        Assert.assertEquals(address, log.getAddress());
        Assert.assertEquals(data, log.getData());
        Assert.assertEquals(3, log.getTopics().size());
    }
}
