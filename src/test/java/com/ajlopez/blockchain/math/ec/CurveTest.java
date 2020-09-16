package com.ajlopez.blockchain.math.ec;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 15/09/2020.
 */
public class CurveTest {
    @Test
    public void createCurve() {
        Curve curve = new Curve(2, 6, 7);

        Assert.assertEquals(2, curve.getA().toLong());
        Assert.assertEquals(6, curve.getB().toLong());
        Assert.assertEquals(7, curve.getP());
    }

    @Test
    public void pointInCurve() {
        Curve curve = new Curve(2, 6, 7);
        Point point = new Point(curve, 1, 3);

        Assert.assertTrue(curve.inCurve(point));
    }

    @Test
    public void pointNotInCurve() {
        Curve curve = new Curve(2, 6, 7);
        Point point = new Point(curve, 1, 2);

        Assert.assertFalse(curve.inCurve(point));
    }
}
