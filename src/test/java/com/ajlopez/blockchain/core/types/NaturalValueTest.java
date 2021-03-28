package com.ajlopez.blockchain.core.types;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigInteger;

/**
 * Created by ajlopez on 03/08/2019.
 */
public class NaturalValueTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void createUsingBigInteger() {
        NaturalValue value = new NaturalValue(BigInteger.TEN);

        Assert.assertEquals(BigInteger.TEN, value.asBigInteger());
    }

    @Test
    public void createUsingBigIntegerZero() {
        NaturalValue value = new NaturalValue(BigInteger.ZERO);

        Assert.assertEquals(BigInteger.ZERO, value.asBigInteger());
    }

    @Test
    public void cannotCreateUsingNegativeBigInteger() {
        exception.expect(ArithmeticException.class);
        exception.expectMessage("Natural value cannot be negative");
        new NaturalValue(BigInteger.TEN.negate());
    }

    @Test
    public void equalsNotEquals() {
        Assert.assertEquals(new NaturalValue(BigInteger.TEN), new NaturalValue(BigInteger.TEN));

        Assert.assertNotEquals(new NaturalValue(BigInteger.TEN), BigInteger.TEN);
        Assert.assertNotEquals(new NaturalValue(BigInteger.TEN), null);
    }

    @Test
    public void compareHashCodes() {
        Assert.assertEquals(new NaturalValue(BigInteger.TEN).hashCode(), new NaturalValue(BigInteger.TEN).hashCode());
        Assert.assertEquals(new NaturalValue(BigInteger.TEN).hashCode(), BigInteger.TEN.hashCode());

        Assert.assertNotEquals(new NaturalValue(BigInteger.TEN).hashCode(), new NaturalValue(BigInteger.ONE));
    }

    @Test
    public void toBytes() {
        Assert.assertArrayEquals(new byte[] { 0x01 }, new NaturalValue(new BigInteger(1, new byte[] { 0x01 })).toBytes());

        byte[] bytes = new byte[NaturalValue.NATURAL_VALUE_BYTES];

        for (int k = 0; k < bytes.length; k++)
            bytes[k] = (byte)0xff;

        Assert.assertArrayEquals(bytes, new NaturalValue(new BigInteger(1, bytes)).toBytes());
    }

    @Test
    public void naturalValueToString() {
        Assert.assertEquals("0x0", new NaturalValue(new BigInteger(1, new byte[] { 0x00, 0x00 })).toString());
        Assert.assertEquals("0x1", new NaturalValue(new BigInteger(1, new byte[] { 0x01 })).toString());
        Assert.assertEquals("0xff", new NaturalValue(new BigInteger(1, new byte[] { (byte)0xff })).toString());
        Assert.assertEquals("0x100", new NaturalValue(new BigInteger(1, new byte[] { 0x01, 0x00 })).toString());
    }
}
