package com.ajlopez.blockchain.math.ec;

import java.math.BigInteger;

/**
 * Created by ajlopez on 16/09/2020.
 */
public class FieldElement {
    private final BigInteger prime;
    private final BigInteger value;

    public FieldElement(BigInteger prime, BigInteger value) {
        this.prime = prime;
        this.value = value;
    }

    public FieldElement add(FieldElement element) {
        return new FieldElement(this.prime, this.value.add(element.value).mod(this.prime));
    }

    public FieldElement subtract(FieldElement element) {
        return this.add(element.negate());
    }

    public FieldElement twice() {
        return new FieldElement(this.prime, this.value.shiftLeft(1).mod(this.prime));
    }

    public FieldElement negate() {
        if (this.value.signum() == 0)
            return this;

        return new FieldElement(this.prime, this.prime.subtract(this.value));
    }

    public FieldElement multiply(FieldElement element) {
        return new FieldElement(this.prime, this.value.multiply(element.value).mod(this.prime));
    }

    // TODO zero case
    // TODO improve cache
    public FieldElement inverse() {
        BigInteger newvalue = this.value.modPow(this.prime.subtract(BigInteger.valueOf(2)), this.prime);

        return new FieldElement(this.prime, newvalue);
    }

    public boolean isZero() {
        return this.value.signum() == 0;
    }

    public BigInteger toBigInteger() {
        return this.value;
    }
}
