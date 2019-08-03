package com.ajlopez.blockchain.core.types;

import com.ajlopez.blockchain.vms.eth.VirtualMachineException;
import com.sun.javaws.exceptions.InvalidArgumentException;
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
}
