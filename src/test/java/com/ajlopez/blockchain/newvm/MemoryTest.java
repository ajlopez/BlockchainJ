package com.ajlopez.blockchain.newvm;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 26/11/2017.
 */
public class MemoryTest {
    @Test
    public void getZeroUndefinedValue() {
        Memory memory = new Memory();

        Assert.assertEquals(0, memory.getValue(0));
        Assert.assertEquals(0, memory.getValue(10));
        Assert.assertEquals(0, memory.getValue(100));
        Assert.assertEquals(0, memory.getValue(1_000_000));
    }

    @Test
    public void setAndGetValue() {
        Memory memory = new Memory();

        memory.setValue(10, (byte)42);

        Assert.assertEquals(0, memory.getValue(0));
        Assert.assertEquals(42, memory.getValue(10));
        Assert.assertEquals(0, memory.getValue(100));
    }


    @Test
    public void setAndGetValueAndGetUndefinedValueAsZero() {
        Memory memory = new Memory();

        memory.setValue(10, (byte)42);

        Assert.assertEquals(0, memory.getValue(0));
        Assert.assertEquals(42, memory.getValue(10));
        Assert.assertEquals(0, memory.getValue(100));

        Assert.assertEquals(0, memory.getValue(100000));
    }

    @Test
    public void setAndGetValues() {
        Memory memory = new Memory();

        memory.setValues(10, new byte[] { 0x01, 0x02, 0x03 });

        Assert.assertEquals(0, memory.getValue(0));
        Assert.assertEquals(1, memory.getValue(10));
        Assert.assertEquals(2, memory.getValue(11));
        Assert.assertEquals(3, memory.getValue(12));
        Assert.assertEquals(0, memory.getValue(100));

        Assert.assertArrayEquals(new byte[] { 0x00, 0x01, 0x02, 0x03, 0x00 }, memory.getValues(9, 5));
    }

    @Test
    public void setAndGetValuesExtendingMemory() {
        Memory memory = new Memory();

        memory.setValues(1021, new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 });

        Assert.assertEquals(0, memory.getValue(0));
        Assert.assertEquals(1, memory.getValue(1021));
        Assert.assertEquals(2, memory.getValue(1022));
        Assert.assertEquals(3, memory.getValue(1023));
        Assert.assertEquals(4, memory.getValue(1024));
        Assert.assertEquals(5, memory.getValue(1025));
        Assert.assertEquals(6, memory.getValue(1026));
        Assert.assertEquals(0, memory.getValue(1027));
    }

    @Test
    public void setAndGetValuesAndGetUndefinedValues() {
        Memory memory = new Memory();

        memory.setValues(10, new byte[] { 0x01, 0x02, 0x03 });

        Assert.assertEquals(0, memory.getValue(0));
        Assert.assertEquals(1, memory.getValue(10));
        Assert.assertEquals(2, memory.getValue(11));
        Assert.assertEquals(3, memory.getValue(12));
        Assert.assertEquals(0, memory.getValue(100));

        Assert.assertArrayEquals(new byte[] { 0x00, 0x01, 0x02, 0x03, 0x00 }, memory.getValues(9, 5));

        Assert.assertArrayEquals(new byte[32], memory.getValues(1024, 32));
        Assert.assertArrayEquals(new byte[32], memory.getValues(1000, 32));
    }
}
