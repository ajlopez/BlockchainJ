package com.ajlopez.blockchain.math.ec;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.math.BigInteger;

/**
 * Created by ajlopez on 18/09/2020.
 */
public class FieldElementTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

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

        Assert.assertTrue(three.add(result).isZero());
    }

    @Test
    public void subtractElement() {
        FieldElement three = new FieldElement(BigInteger.valueOf(7), BigInteger.valueOf(3));
        FieldElement two = new FieldElement(BigInteger.valueOf(7), BigInteger.valueOf(2));

        FieldElement result = three.subtract(two);

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.valueOf(1), result.toBigInteger());
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

    @Test
    public void inverseZeroElement() {
        FieldElement zero = new FieldElement(BigInteger.valueOf(7), BigInteger.valueOf(0));

        exception.expect(ArithmeticException.class);
        exception.expectMessage("Zero has no inverse");
        zero.inverse();
    }

    @Test
    public void divideElement() {
        FieldElement three = new FieldElement(BigInteger.valueOf(7), BigInteger.valueOf(3));
        FieldElement five = new FieldElement(BigInteger.valueOf(7), BigInteger.valueOf(5));

        FieldElement result = three.divide(five);

        Assert.assertNotNull(result);
        Assert.assertEquals(BigInteger.valueOf(2), result.toBigInteger());
        Assert.assertFalse(result.isZero());
        Assert.assertEquals(BigInteger.ONE, three.divide(three).toBigInteger());
    }
}
