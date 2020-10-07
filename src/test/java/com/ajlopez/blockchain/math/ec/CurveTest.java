package com.ajlopez.blockchain.math.ec;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created by ajlopez on 15/09/2020.
 */
public class CurveTest {
    @Test
    public void createCurve() {
        Curve curve = new Curve(BigInteger.valueOf(2), BigInteger.valueOf(6), BigInteger.valueOf(7));

        Assert.assertEquals(BigInteger.valueOf(2), curve.getA().toBigInteger());
        Assert.assertEquals(BigInteger.valueOf(6), curve.getB().toBigInteger());
        Assert.assertEquals(BigInteger.valueOf(7), curve.getP());
    }

    @Test
    public void getInfinitePoint() {
        Curve curve = new Curve(BigInteger.valueOf(2), BigInteger.valueOf(6), BigInteger.valueOf(7));

        Point result = curve.getInfinite();

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isInfinite());
        Assert.assertTrue(curve.inCurve(result));
    }

    @Test
    public void pointInCurve() {
        Curve curve = new Curve(BigInteger.valueOf(2), BigInteger.valueOf(6), BigInteger.valueOf(7));
        Point point = Point.fromBigIntegers(curve, BigInteger.ONE, BigInteger.valueOf(3));

        Assert.assertTrue(curve.inCurve(point));
    }

    @Test
    public void infinitePointInCurve() {
        Curve curve = new Curve(BigInteger.valueOf(2), BigInteger.valueOf(6), BigInteger.valueOf(7));
        Point point = curve.getInfinite();

        Assert.assertTrue(curve.inCurve(point));
    }

    @Test
    public void pointNotInCurve() {
        Curve curve = new Curve(BigInteger.valueOf(2), BigInteger.valueOf(6), BigInteger.valueOf(7));
        Point point = Point.fromBigIntegers(curve, BigInteger.ONE, BigInteger.valueOf(2));

        Assert.assertFalse(curve.inCurve(point));
    }
}
