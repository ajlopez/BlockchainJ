package com.ajlopez.blockchain.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 04/01/2018.
 */
public class ByteUtilsTest {
    @Test
    public void zeroByteToUnsignedInteger() {
        Assert.assertEquals(0, ByteUtils.bytesToUnsignedInteger(new byte[1], 0));
    }

    @Test
    public void zeroByte2ToUnsignedInteger() {
        Assert.assertEquals(0, ByteUtils.bytesToUnsignedInteger(new byte[1], 0));
    }

    @Test
    public void manyZeroBytesToUnsignedInteger() {
        Assert.assertEquals(0, ByteUtils.bytesToUnsignedInteger(new byte[32], 0));
    }

    @Test
    public void zeroBytesAreZero() {
        Assert.assertTrue(ByteUtils.areZero(new byte[0]));
        Assert.assertTrue(ByteUtils.areZero(new byte[1]));
        Assert.assertTrue(ByteUtils.areZero(new byte[2]));
        Assert.assertTrue(ByteUtils.areZero(new byte[32]));
    }

    @Test
    public void otherBytesAreNotZero() {
        Assert.assertFalse(ByteUtils.areZero(new byte[] { 0x01 }));
        Assert.assertFalse(ByteUtils.areZero(new byte[] { 0x0, 0x01 }));
        Assert.assertFalse(ByteUtils.areZero(new byte[] { 0x0, 0x00, (byte)0xff }));
    }

    @Test
    public void unsignedShortOneToBytes() {
        byte[] result = ByteUtils.unsignedShortToBytes((short)1);

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.length);
        Assert.assertEquals(0, result[0]);
        Assert.assertEquals(1, result[1]);
    }

    @Test
    public void unsignedShort256ToBytes() {
        byte[] result = ByteUtils.unsignedShortToBytes((short)256);

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.length);
        Assert.assertEquals(1, result[0]);
        Assert.assertEquals(0, result[1]);
    }

    @Test
    public void unsignedIntegerOneToBytes() {
        byte[] result = ByteUtils.unsignedIntegerToBytes(1);

        Assert.assertNotNull(result);
        Assert.assertEquals(Integer.BYTES, result.length);
        Assert.assertEquals(0, result[0]);
        Assert.assertEquals(0, result[1]);
        Assert.assertEquals(0, result[2]);
        Assert.assertEquals(1, result[3]);
    }

    @Test
    public void unsignedInteger256ToBytes() {
        byte[] result = ByteUtils.unsignedIntegerToBytes(256);

        Assert.assertNotNull(result);
        Assert.assertEquals(Integer.BYTES, result.length);
        Assert.assertEquals(0, result[0]);
        Assert.assertEquals(0, result[1]);
        Assert.assertEquals(1, result[2]);
        Assert.assertEquals(0, result[3]);
    }

    @Test
    public void unsignedIntegerOneToBytesUsingArray() {
        byte[] result = new byte[Integer.BYTES];
        ByteUtils.unsignedIntegerToBytes(1, result, 0);

        Assert.assertEquals(Integer.BYTES, result.length);
        Assert.assertEquals(0, result[0]);
        Assert.assertEquals(0, result[1]);
        Assert.assertEquals(0, result[2]);
        Assert.assertEquals(1, result[3]);
    }

    @Test
    public void unsignedInteger256ToBytesUsingArray() {
        byte[] result = new byte[Integer.BYTES];
        ByteUtils.unsignedIntegerToBytes(256, result, 0);

        Assert.assertEquals(Integer.BYTES, result.length);
        Assert.assertEquals(0, result[0]);
        Assert.assertEquals(0, result[1]);
        Assert.assertEquals(1, result[2]);
        Assert.assertEquals(0, result[3]);
    }

    @Test
    public void copyBytes() {
        byte[] bytes = new byte[] { 0x01, 0x02, 0x03, 0x05 };

        byte[] result = ByteUtils.copyBytes(bytes);

        Assert.assertNotNull(result);
        Assert.assertNotSame(bytes, result);
        Assert.assertArrayEquals(bytes, result);
    }

    @Test
    public void copyBytesWithLeftPadding() {
        byte[] bytes = new byte[] { 0x01, 0x02, 0x03, 0x05 };
        byte[] expected = new byte[] { 0x00, 0x00, 0x01, 0x02, 0x03, 0x05 };

        byte[] result = ByteUtils.copyBytes(bytes, expected.length);

        Assert.assertNotNull(result);
        Assert.assertNotSame(bytes, result);
        Assert.assertNotSame(expected, result);
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void copyBytesWithRightPadding() {
        byte[] bytes = new byte[] { 0x01, 0x02, 0x03, 0x05 };
        byte[] expected = new byte[] { 0x01, 0x02, 0x03, 0x05, 0x00, 0x00 };

        byte[] result = ByteUtils.copyBytes(bytes, expected.length, true, false);

        Assert.assertNotNull(result);
        Assert.assertNotSame(bytes, result);
        Assert.assertNotSame(expected, result);
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void numberOfLeadingZeroes() {
        Assert.assertEquals(0, ByteUtils.numberOfLeadingZeroes(new byte[0]));
        Assert.assertEquals(0, ByteUtils.numberOfLeadingZeroes(new byte[] { 0x01 }));
        Assert.assertEquals(0, ByteUtils.numberOfLeadingZeroes(new byte[] { 0x01, 0x02 }));

        Assert.assertEquals(1, ByteUtils.numberOfLeadingZeroes(new byte[] { 0x00 }));
        Assert.assertEquals(1, ByteUtils.numberOfLeadingZeroes(new byte[] { 0x00, 0x01, 0x00 }));

        Assert.assertEquals(2, ByteUtils.numberOfLeadingZeroes(new byte[] { 0x00, 0x00 }));
        Assert.assertEquals(2, ByteUtils.numberOfLeadingZeroes(new byte[] { 0x00, 0x00, 0x01, 0x00 }));
    }

    @Test
    public void removeLeadingZeroes() {
        Assert.assertArrayEquals(new byte[] { 0x01, 0x02, 0x03 }, ByteUtils.removeLeadingZeroes(new byte[] { 0x01, 0x02, 0x03 }));
        Assert.assertArrayEquals(new byte[] { 0x01, 0x02, 0x03 }, ByteUtils.removeLeadingZeroes(new byte[] { 0x00, 0x01, 0x02, 0x03 }));
        Assert.assertArrayEquals(new byte[] { 0x01, 0x02, 0x03 }, ByteUtils.removeLeadingZeroes(new byte[] { 0x00, 0x00, 0x00, 0x01, 0x02, 0x03 }));
        Assert.assertArrayEquals(new byte[] { }, ByteUtils.removeLeadingZeroes(new byte[] { 0x00, 0x00, 0x00, 0x00 }));
    }

    @Test
    public void normalizedBytes() {
        Assert.assertArrayEquals(new byte[] { 0x01, 0x02, 0x03 }, ByteUtils.normalizedBytes(new byte[] { 0x01, 0x02, 0x03 }));
        Assert.assertArrayEquals(new byte[] { 0x01, 0x02, 0x03 }, ByteUtils.normalizedBytes(new byte[] { 0x00, 0x01, 0x02, 0x03 }));
        Assert.assertArrayEquals(new byte[] { 0x01, 0x02, 0x03 }, ByteUtils.normalizedBytes(new byte[] { 0x00, 0x00, 0x00, 0x01, 0x02, 0x03 }));
        Assert.assertArrayEquals(new byte[] { 0x00 }, ByteUtils.normalizedBytes(new byte[] { 0x00, 0x00, 0x00, 0x00 }));
    }

    @Test
    public void unsignedLongToBytes() {
        Assert.assertArrayEquals(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }, ByteUtils.unsignedLongToBytes(0));
        Assert.assertArrayEquals(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01 }, ByteUtils.unsignedLongToBytes(1));
        Assert.assertArrayEquals(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02 }, ByteUtils.unsignedLongToBytes(2));
        Assert.assertArrayEquals(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte)0xff }, ByteUtils.unsignedLongToBytes(255));
        Assert.assertArrayEquals(new byte[] { 0x7f, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff }, ByteUtils.unsignedLongToBytes(Long.MAX_VALUE));
    }

    @Test
    public void unsignedLongToNormalizedBytes() {
        Assert.assertArrayEquals(new byte[] { 0x00 }, ByteUtils.unsignedLongToNormalizedBytes(0));
        Assert.assertArrayEquals(new byte[] { 0x01 }, ByteUtils.unsignedLongToNormalizedBytes(1));
        Assert.assertArrayEquals(new byte[] { 0x02 }, ByteUtils.unsignedLongToNormalizedBytes(2));
        Assert.assertArrayEquals(new byte[] { (byte)0xff }, ByteUtils.unsignedLongToNormalizedBytes(255));
        Assert.assertArrayEquals(new byte[] { 0x7f, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff }, ByteUtils.unsignedLongToNormalizedBytes(Long.MAX_VALUE));
    }
}
