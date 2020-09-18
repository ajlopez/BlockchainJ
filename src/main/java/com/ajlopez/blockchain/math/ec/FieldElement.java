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

    public FieldElement negate() {
        if (this.value.signum() == 0)
            return this;

        return new FieldElement(this.prime, this.prime.subtract(this.value));
    }

    public FieldElement multiply(FieldElement element) {
        return new FieldElement(this.prime, this.value.multiply(element.value).mod(this.prime));
    }

    public BigInteger toBigInteger() {
        return this.value;
    }
}
