package com.ajlopez.blockchain.math.ec;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 15/09/2020.
 */
public class PointTest {
    @Test
    public void createPoint() {
        Curve curve = new Curve(2, 6, 7);

        Point point = new Point(curve, 1, 3);

        Assert.assertSame(curve, point.getCurve());
        Assert.assertEquals(1, point.getX().toLong());
        Assert.assertEquals(3, point.getY().toLong());
    }
}
