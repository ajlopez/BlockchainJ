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
}
