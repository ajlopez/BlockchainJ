package com.ajlopez.blockchain.core.types;

import java.math.BigInteger;

/**
 * Created by ajlopez on 03/08/2019.
 */
public class NaturalValue {
    private final BigInteger value;

    public NaturalValue(BigInteger value) {
        if (value.signum() < 0)
            throw new ArithmeticException("Natural value cannot be negative");

        this.value = value;
    }

    public BigInteger asBigInteger() { return this.value; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (this == obj)
            return true;

        if (this.getClass() != obj.getClass())
            return false;

        return this.value.equals(((NaturalValue)obj).value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
}
