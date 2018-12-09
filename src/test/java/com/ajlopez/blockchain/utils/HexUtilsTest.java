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

        String result = HexUtils.bytesToHexString(bytes);

        Assert.assertNotNull(result);
        Assert.assertEquals("0123456789abcdef", result);
    }

    @Test
    public void convertBytesToHexStringWithPrefix() {
        byte[] bytes = new byte[] { 0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef };

        String result = HexUtils.bytesToHexString(bytes, true);

        Assert.assertNotNull(result);
        Assert.assertEquals("0x0123456789abcdef", result);
    }

    @Test
    public void convertHexadecimalStringToBytes() {
        byte[] result = HexUtils.hexStringToBytes("ff0102");

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(new byte[] { (byte)0xff, 0x01, 0x02 }, result);
    }

    @Test
    public void convertHexadecimalStringToBytesUsingUppercaseHexadecimalDigits() {
        byte[] result = HexUtils.hexStringToBytes("FF0102");

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(new byte[] { (byte)0xff, 0x01, 0x02 }, result);
    }

    @Test
    public void convertHexadecimalStringToBytesUsingPrefix() {
        byte[] result = HexUtils.hexStringToBytes("0xff0102");

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(new byte[] { (byte)0xff, 0x01, 0x02 }, result);
    }

    @Test
    public void convertHexadecimalStringToBytesUsingUppercasePrefix() {
        byte[] result = HexUtils.hexStringToBytes("0Xff0102");

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(new byte[] { (byte)0xff, 0x01, 0x02 }, result);
    }
}
