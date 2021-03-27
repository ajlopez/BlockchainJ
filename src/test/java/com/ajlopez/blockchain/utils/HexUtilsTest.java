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

        String result = HexUtils.bytesToHexString(bytes, true, false);

        Assert.assertNotNull(result);
        Assert.assertEquals("0x0123456789abcdef", result);
    }

    @Test
    public void convertBytesToHexStringWithoutPrefixWithoutLeadingZeroes() {
        byte[] bytes = new byte[] { 0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef };

        String result = HexUtils.bytesToHexString(bytes, false, true);

        Assert.assertNotNull(result);
        Assert.assertEquals("123456789abcdef", result);
    }

    @Test
    public void convertBytesToHexStringWithPrefixWithoutLeadingZeroes() {
        byte[] bytes = new byte[] { 0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef };

        String result = HexUtils.bytesToHexString(bytes, true, true);

        Assert.assertNotNull(result);
        Assert.assertEquals("0x123456789abcdef", result);
    }

    @Test
    public void convertZeroBytesToHexStringWithPrefixWithoutLeadingZeroes() {
        byte[] bytes = new byte[] { 0x00, 0x00 };

        String result = HexUtils.bytesToHexString(bytes, true, true);

        Assert.assertNotNull(result);
        Assert.assertEquals("0x0", result);
    }

    @Test
    public void convertHexadecimalStringToBytes() {
        byte[] result = HexUtils.hexStringToBytes("ff0102");

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(new byte[] { (byte)0xff, 0x01, 0x02 }, result);
    }

    @Test
    public void convertHexadecimalStringWithOddQuantityOfDigitsToBytes() {
        byte[] result = HexUtils.hexStringToBytes("f0102");

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(new byte[] { (byte)0x0f, 0x01, 0x02 }, result);
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

    @Test
    public void convertUnsignedLongToHexValue() {
        Assert.assertEquals("0x0", HexUtils.unsignedLongToHexValue(0L));
        Assert.assertEquals("0x1", HexUtils.unsignedLongToHexValue(1L));
        Assert.assertEquals("0xa", HexUtils.unsignedLongToHexValue(10L));
        Assert.assertEquals("0x2a", HexUtils.unsignedLongToHexValue(42L));
        Assert.assertEquals("0xff", HexUtils.unsignedLongToHexValue(255L));
        Assert.assertEquals("0x100", HexUtils.unsignedLongToHexValue(256L));
    }
}
