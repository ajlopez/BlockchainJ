package com.ajlopez.blockchain.math.ec;

/**
 * Created by ajlopez on 15/09/2020.
 */
public class Curve {
    private final long a;
    private final long b;
    private final long p;

    public Curve(long a, long b, long p) {
        this.a = a;
        this.b = b;
        this.p = p;
    }

    public long getA() { return this.a; }

    public long getB() { return this.b; }

    public long getP() { return this.p; }

    public boolean inCurve(Point point) {
        long y = point.getY();
        long x = point.getX();

        y *= y;
        y %= this.p;

        long right = (x * x + this.a * x + this.b) % this.p;

        return y == right;
    }
}
