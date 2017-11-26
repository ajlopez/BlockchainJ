package com.ajlopez.blockchain.vm;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 26/11/2017.
 */
public class MemoryTest {
    @Test
    public void getNullForUndefinedValue() {
        Memory memory = new Memory();

        Assert.assertNull(memory.getValue(new byte[] { 0x01, 0x02, 0x03 }));
    }

    @Test
    public void setAndGetValue() {
        byte[] key = new byte[] { 0x01, 0x02, 0x03 };
        byte[] value = new byte[] { 0x04, 0x05, 0x06 };
        Memory memory = new Memory();

        memory.setValue(key, value);

        Assert.assertArrayEquals(value, memory.getValue(key));
    }

    @Test
    public void setAndGetValueWithLeadingZeroesInKey() {
        byte[] key = new byte[] { 0x00, 0x00, 0x01, 0x02, 0x03 };
        byte[] key2 = new byte[] { 0x00, 0x01, 0x02, 0x03 };
        byte[] key3 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] value = new byte[] { 0x04, 0x05, 0x06 };
        Memory memory = new Memory();

        memory.setValue(key, value);

        Assert.assertArrayEquals(value, memory.getValue(key));
        Assert.assertArrayEquals(value, memory.getValue(key2));
        Assert.assertArrayEquals(value, memory.getValue(key3));
    }
}
