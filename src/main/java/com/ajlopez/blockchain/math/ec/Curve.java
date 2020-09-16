package com.ajlopez.blockchain.math.ec;

/**
 * Created by ajlopez on 15/09/2020.
 */
public class Curve {
    private final FieldElement a;
    private final FieldElement b;
    private final long p;

    public Curve(long a, long b, long p) {
        this.a = new FieldElement(p, a);
        this.b = new FieldElement(p, b);
        this.p = p;
    }

    public FieldElement getA() { return this.a; }

    public FieldElement getB() { return this.b; }

    public long getP() { return this.p; }

    public boolean inCurve(Point point) {
        FieldElement y = point.getY();
        FieldElement x = point.getX();

        y = y.multiply(y);

        FieldElement right = x.multiply(x).add(this.a.multiply(x)).add(this.b);

        return y.toLong() == right.toLong();
    }
}
