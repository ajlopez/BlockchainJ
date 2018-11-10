package com.ajlopez.blockchain.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 10/11/2018.
 */
public class HexUtilsTest {
    @Test
    public void convertBytesToHexString() {
        byte[] bytes = new byte[] { 0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef };

        String result = HexUtils.bytestoHexString(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals("0123456789abcdef", result);
    }
}
