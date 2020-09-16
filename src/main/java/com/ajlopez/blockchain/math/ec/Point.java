package com.ajlopez.blockchain.math.ec;

/**
 * Created by ajlopez on 15/09/2020.
 */
public class Point {
    private final Curve curve;
    private final FieldElement x;
    private final FieldElement y;

    public Point(Curve curve, long x, long y) {
        this.curve = curve;
        this.x = new FieldElement(curve.getP(), x);
        this.y = new FieldElement(curve.getP(), y);
    }

    public Curve getCurve() { return this.curve; }

    public FieldElement getX() { return this.x; }

    public FieldElement getY() { return this.y; }
}
