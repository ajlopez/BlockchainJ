package com.ajlopez.blockchain.math.ec;

import java.math.BigInteger;

/**
 * Created by ajlopez on 15/09/2020.
 */
public class Curve {
    private final FieldElement a;
    private final FieldElement b;
    private final BigInteger p;

    public Curve(BigInteger a, BigInteger b, BigInteger p) {
        this.a = new FieldElement(p, a);
        this.b = new FieldElement(p, b);
        this.p = p;
    }

    public FieldElement getA() { return this.a; }

    public FieldElement getB() { return this.b; }

    public BigInteger getP() { return this.p; }

    public boolean inCurve(Point point) {
        if (point.isInfinite())
            return true;

        FieldElement y = point.getY();
        FieldElement x = point.getX();

        y = y.multiply(y);

        FieldElement right = x.multiply(x).add(this.a.multiply(x)).add(this.b);

        // TODO Maybe implement FieldElement.equals
        return y.toBigInteger().equals(right.toBigInteger());
    }
}
