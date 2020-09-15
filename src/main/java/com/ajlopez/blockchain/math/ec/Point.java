package com.ajlopez.blockchain.math.ec;

/**
 * Created by ajlopez on 15/09/2020.
 */
public class Point {
    private final Curve curve;
    private final long x;
    private final long y;

    public Point(Curve curve, long x, long y) {
        this.curve = curve;
        this.x = x;
        this.y = y;
    }

    public Curve getCurve() { return this.curve; }

    public long getX() { return this.x; }

    public long getY() { return this.y; }
}
