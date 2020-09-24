package com.ajlopez.blockchain.math.ec;

import java.math.BigInteger;

/**
 * Created by ajlopez on 24/09/2020.
 */
public class Field {
    private final BigInteger prime;

    public Field(BigInteger prime) {
        this.prime = prime;
    }

    public BigInteger getPrime() {
        return this.prime;
    }
}
