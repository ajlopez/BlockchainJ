package com.ajlopez.blockchain.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by usuario on 21/11/2017.
 */
public class ByteArrayWrapperTest {
    @Test
    public void createWithData() {
        byte[] data = new byte[] { 0x01, 0x02, 0x03 };

        ByteArrayWrapper wrapper = new ByteArrayWrapper(data);

        Assert.assertArrayEquals(data, wrapper.getBytes());
    }

    @Test
    public void createWithSameData() {
        byte[] data1 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] data2 = new byte[] { 0x01, 0x02, 0x03 };

        ByteArrayWrapper wrapper1 = new ByteArrayWrapper(data1);
        ByteArrayWrapper wrapper2 = new ByteArrayWrapper(data2);

        Assert.assertEquals(wrapper1, wrapper2);
        Assert.assertEquals(wrapper1.hashCode(), wrapper2.hashCode());

        Assert.assertTrue(wrapper1.equals(wrapper2));
        Assert.assertTrue(wrapper2.equals(wrapper1));

        Assert.assertFalse(wrapper1.equals(null));
        Assert.assertFalse(wrapper1.equals("foo"));
    }
}
