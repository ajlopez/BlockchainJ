package com.ajlopez.blockchain.math.ec;

import java.math.BigInteger;

/**
 * Created by ajlopez on 15/09/2020.
 */
public class Point {
    private final Curve curve;
    private final FieldElement x;
    private final FieldElement y;
    private final boolean isInfinite;

    public Point(Curve curve, BigInteger x, BigInteger y) {
        this.curve = curve;

        if (x == null && y == null) {
            this.x = null;
            this.y = null;
            this.isInfinite = true;
        }
        else {
            this.x = new FieldElement(curve.getP(), x);
            this.y = new FieldElement(curve.getP(), y);
            this.isInfinite = false;
        }
    }

    public Curve getCurve() { return this.curve; }

    public FieldElement getX() { return this.x; }

    public FieldElement getY() { return this.y; }

    public boolean isInfinite() { return this.isInfinite; }
}
