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

        long position = valueFile.writeValue(value);

        Assert.assertEquals(0L, position);

        byte[] result = new byte[value.length];

        int nread = valueFile.readValue(position, result);

        Assert.assertEquals(value.length, nread);
        Assert.assertArrayEquals(value, result);
    }

    @Test
    public void writeCloseReopenAndReadValue() throws IOException {
        ValueFile valueFile = new ValueFile("vftest2.data");

        byte[] value = FactoryHelper.createRandomBytes(42);

        long position = valueFile.writeValue(value);
        valueFile.close();

        ValueFile valueFile2 = new ValueFile("vftest2.data");

        byte[] result = new byte[value.length];

        int nread = valueFile2.readValue(position, result);

        Assert.assertEquals(value.length, nread);
        Assert.assertArrayEquals(value, result);
    }

    @Test
    public void writeAndReadTwoValues() throws IOException {
        ValueFile valueFile = new ValueFile("vftest3.data");

        byte[] value1 = FactoryHelper.createRandomBytes(42);
        byte[] value2 = FactoryHelper.createRandomBytes(100);

        long position1 = valueFile.writeValue(value1);
        long position2 = valueFile.writeValue(value2);

        Assert.assertEquals(0L, position1);
        Assert.assertEquals(value1.length, position2);

        byte[] result1 = new byte[value1.length];
        byte[] result2 = new byte[value2.length];

        int nread1 = valueFile.readValue(position1, result1);
        int nread2 = valueFile.readValue(position2, result2);

        Assert.assertEquals(value1.length, nread1);
        Assert.assertArrayEquals(value1, result1);

        Assert.assertEquals(value2.length, nread2);
        Assert.assertArrayEquals(value2, result2);
    }
}
