package com.ajlopez.blockchain.math.ec;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigInteger;

/**
 * Created by ajlopez on 15/09/2020.
 */
public class Point {
    private final Curve curve;
    private final FieldElement x;
    private final FieldElement y;
    private final boolean isInfinite;

    // TODO Unify constructors
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

    public Point(Curve curve, FieldElement x, FieldElement y) {
        this.curve = curve;

        if (x == null && y == null) {
            this.x = null;
            this.y = null;
            this.isInfinite = true;
        }
        else {
            this.x = x;
            this.y = y;
            this.isInfinite = false;
        }
    }

    public Curve getCurve() { return this.curve; }

    public FieldElement getX() { return this.x; }

    public FieldElement getY() { return this.y; }

    public boolean isInfinite() { return this.isInfinite; }

    public Point negate() {
        if (this.isInfinite)
            return this;

        return new Point(this.curve, this.x, this.y.negate());
    }

    public Point add(Point point) {
        if (this.isInfinite)
            return point;

        if (point.isInfinite())
            return this;

        FieldElement minusX2 = point.x.negate();
        FieldElement dX = this.x.add(minusX2);
        FieldElement dY = this.y.add(point.y.negate());

        if (dX.isZero())
            if (dY.isZero() && !this.y.isZero())
                return twice();
            else
                return new Point(this.curve, (FieldElement)null, (FieldElement)null);

        FieldElement s = dY.multiply(dX.inverse());
        FieldElement minusX1 = this.x.negate();
        FieldElement x3 = s.multiply(s).add(minusX1).add(minusX2);
        FieldElement y3 = s.multiply(this.x.add(minusX2)).add(this.y.negate());

        return new Point(this.curve, x3, y3);
    }

    public Point twice() {
        FieldElement g = this.x.multiply(this.x).multiply(new FieldElement(this.curve.getP(), BigInteger.valueOf(3)));

        g = g.add(this.curve.getA());
        g = g.multiply(this.y.multiply(new FieldElement(this.curve.getP(), BigInteger.valueOf(2))).inverse());

        FieldElement x3 = g.multiply(g);

        x3 = x3.add(this.x.multiply(new FieldElement(this.curve.getP(), BigInteger.valueOf(2))).negate());

        FieldElement y3 = g.multiply(this.x.add(x3.negate())).add(this.y.negate());

        return new Point(this.curve, x3, y3);
    }
}

