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

        Point point = Point.fromBigIntegers(curve, BigInteger.ONE, BigInteger.valueOf(3));

        Assert.assertSame(curve, point.getCurve());
        Assert.assertEquals(BigInteger.ONE, point.getX().toBigInteger());
        Assert.assertEquals(BigInteger.valueOf(3), point.getY().toBigInteger());

        Assert.assertFalse(point.isInfinite());
    }

    @Test
    public void negatePoint() {
        Curve curve = new Curve(BigInteger.valueOf(2), BigInteger.valueOf(6), BigInteger.valueOf(7));

        Point point = Point.fromBigIntegers(curve, BigInteger.ONE, BigInteger.valueOf(3)).negate();

        Assert.assertSame(curve, point.getCurve());
        Assert.assertEquals(BigInteger.ONE, point.getX().toBigInteger());
        Assert.assertEquals(BigInteger.valueOf(4), point.getY().toBigInteger());

        Assert.assertFalse(point.isInfinite());
    }

    @Test
    public void addPointToNegatedPoint() {
        Curve curve = new Curve(BigInteger.valueOf(2), BigInteger.valueOf(6), BigInteger.valueOf(7));

        Point point = Point.fromBigIntegers(curve, BigInteger.ONE, BigInteger.valueOf(3));
        Point negated = point.negate();

        Point result = point.add(negated);

        Assert.assertNotNull(result);
        Assert.assertSame(curve, result.getCurve());
        Assert.assertTrue(result.isInfinite());
    }

    @Test
    public void negateInfinitePoint() {
        Curve curve = new Curve(BigInteger.valueOf(2), BigInteger.valueOf(6), BigInteger.valueOf(7));

        Point point = curve.getInfinite().negate();

        Assert.assertSame(curve, point.getCurve());

        Assert.assertTrue(point.isInfinite());
    }

    @Test
    public void createInfinitePoint() {
        Curve curve = new Curve(BigInteger.valueOf(2), BigInteger.valueOf(6), BigInteger.valueOf(7));

        Point point = new Point(curve, null, null);

        Assert.assertSame(curve, point.getCurve());
        Assert.assertNull(point.getX());
        Assert.assertNull(point.getY());
        Assert.assertTrue(point.isInfinite());
    }

    @Test
    public void createInfinitePointUsingFieldElements() {
        Curve curve = new Curve(BigInteger.valueOf(2), BigInteger.valueOf(6), BigInteger.valueOf(7));

        Point point = new Point(curve, (FieldElement) null, (FieldElement) null);

        Assert.assertSame(curve, point.getCurve());
        Assert.assertNull(point.getX());
        Assert.assertNull(point.getY());
        Assert.assertTrue(point.isInfinite());
    }

    @Test
    public void addInfinitePointToPoint() {
        Curve curve = new Curve(BigInteger.valueOf(2), BigInteger.valueOf(6), BigInteger.valueOf(7));

        Point infinite = new Point(curve,null,null);
        Point point = Point.fromBigIntegers(curve, BigInteger.ONE, BigInteger.valueOf(3));

        Point result = infinite.add(point);

        Assert.assertNotNull(result);
        Assert.assertSame(point, result);
    }

    @Test
    public void addPointToInfinitePoint() {
        Curve curve = new Curve(BigInteger.valueOf(2), BigInteger.valueOf(6), BigInteger.valueOf(7));

        Point infinite = curve.getInfinite();
        Point point = Point.fromBigIntegers(curve, BigInteger.ONE, BigInteger.valueOf(3));

        Point result = point.add(infinite);

        Assert.assertNotNull(result);
        Assert.assertSame(point, result);
    }

    @Test
    public void addPointWithZeroYToItselfGivingInfinitePoint() {
        Curve curve = new Curve(BigInteger.valueOf(2), BigInteger.valueOf(6), BigInteger.valueOf(7));

        Point point = Point.fromBigIntegers(curve, BigInteger.valueOf(2), BigInteger.ZERO);

        Point result = point.add(point);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isInfinite());
    }

    @Test
    public void addTwoPoints() {
        Curve curve = new Curve(BigInteger.ZERO, BigInteger.valueOf(7), BigInteger.valueOf(37));

        Point point1 = Point.fromBigIntegers(curve, BigInteger.valueOf(6), BigInteger.ONE);
        Point point2 = Point.fromBigIntegers(curve, BigInteger.valueOf(8), BigInteger.ONE);

        Assert.assertTrue(curve.inCurve(point1));
        Assert.assertTrue(curve.inCurve(point2));

        Point result = point1.add(point2);

        Assert.assertNotNull(result);
        Assert.assertTrue(curve.inCurve(result));
        Assert.assertFalse(result.isInfinite());
        Assert.assertEquals(BigInteger.valueOf(23), result.getX().toBigInteger());
        Assert.assertEquals(BigInteger.valueOf(36), result.getY().toBigInteger());
    }

    @Test
    public void addTheSamePoint() {
        Curve curve = new Curve(BigInteger.ZERO, BigInteger.valueOf(7), BigInteger.valueOf(37));

        Point point = Point.fromBigIntegers(curve, BigInteger.valueOf(6), BigInteger.ONE);

        Assert.assertTrue(curve.inCurve(point));

        Point result = point.add(point);

        Assert.assertNotNull(result);
        Assert.assertTrue(curve.inCurve(result));
        Assert.assertFalse(result.isInfinite());
        Assert.assertEquals(BigInteger.valueOf(18), result.getX().toBigInteger());
        Assert.assertEquals(BigInteger.valueOf(17), result.getY().toBigInteger());
    }

    @Test
    public void twicePoint() {
        Curve curve = new Curve(BigInteger.ZERO, BigInteger.valueOf(7), BigInteger.valueOf(37));

        Point point = Point.fromBigIntegers(curve, BigInteger.valueOf(6), BigInteger.ONE);

        Assert.assertTrue(curve.inCurve(point));

        Point result = point.twice();

        Assert.assertNotNull(result);
        Assert.assertTrue(curve.inCurve(result));
        Assert.assertFalse(result.isInfinite());
        Assert.assertEquals(BigInteger.valueOf(18), result.getX().toBigInteger());
        Assert.assertEquals(BigInteger.valueOf(17), result.getY().toBigInteger());
    }
}

