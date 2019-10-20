package com.ajlopez.blockchain.db;

import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 20/10/2019.
 */
public class ValueFileTest {
    @Test
    public void writeAndReadValue() throws IOException {
        ValueFile valueFile = new ValueFile("vftest1.data");

        byte[] value = FactoryHelper.createRandomBytes(42);

        valueFile.writeValue(value, 0L);

        byte[] result = new byte[value.length];

        int nread = valueFile.readValue(0, result);

        Assert.assertEquals(value.length, nread);
        Assert.assertArrayEquals(value, result);
    }

    @Test
    public void writeAndReadTwoValues() throws IOException {
        ValueFile valueFile = new ValueFile("vftest2.data");

        byte[] value1 = FactoryHelper.createRandomBytes(42);
        byte[] value2 = FactoryHelper.createRandomBytes(100);

        valueFile.writeValue(value1, 0L);
        valueFile.writeValue(value2, value1.length);

        byte[] result1 = new byte[value1.length];
        byte[] result2 = new byte[value2.length];

        int nread1 = valueFile.readValue(0, result1);
        int nread2 = valueFile.readValue(value1.length, result2);

        Assert.assertEquals(value1.length, nread1);
        Assert.assertArrayEquals(value1, result1);

        Assert.assertEquals(value2.length, nread2);
        Assert.assertArrayEquals(value2, result2);
    }
}
