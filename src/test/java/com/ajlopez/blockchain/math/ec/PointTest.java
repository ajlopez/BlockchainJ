package com.ajlopez.blockchain.math.ec;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 15/09/2020.
 */
public class PointTest {
    @Test
    public void createPoint() {
        Curve curve = new Curve(BigInteger.valueOf(2), BigInteger.valueOf(6), BigInteger.valueOf(7));

        Point point = new Point(curve, BigInteger.ONE, BigInteger.valueOf(3));

        Assert.assertSame(curve, point.getCurve());
        Assert.assertEquals(BigInteger.ONE, point.getX().toBigInteger());
        Assert.assertEquals(BigInteger.valueOf(3), point.getY().toBigInteger());
    }
}
