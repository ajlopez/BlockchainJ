package com.ajlopez.blockchain.math.ec;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 18/09/2020.
 */
public class FieldElementTest {
    @Test
    public void negateZero() {
        FieldElement zero = new FieldElement(BigInteger.valueOf(7), BigInteger.ZERO);

        FieldElement result = zero.negate();

        Assert.assertNotNull(result);
        Assert.assertSame(zero, result);
        Assert.assertEquals(BigInteger.ZERO, result.toBigInteger());
        Assert.assertTrue(result.isZero());
    }

    @Test
    public void negateElement() {
        FieldElement three = new FieldElement(BigInteger.valueOf(7), BigInteger.valueOf(3));

        FieldElement result = three.negate();

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.valueOf(4), result.toBigInteger());
        Assert.assertFalse(result.isZero());
    }

    @Test
    public void inverseElement() {
        FieldElement three = new FieldElement(BigInteger.valueOf(7), BigInteger.valueOf(3));

        FieldElement result = three.inverse();

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.valueOf(5), result.toBigInteger());
        Assert.assertFalse(result.isZero());
        Assert.assertEquals(BigInteger.ONE, result.multiply(three).toBigInteger());
    }
}