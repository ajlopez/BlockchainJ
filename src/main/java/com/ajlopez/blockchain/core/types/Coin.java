package com.ajlopez.blockchain.core.types;

import java.math.BigInteger;

/**
 * Created by ajlopez on 03/08/2019.
 */
public class Coin extends NaturalValue {
    public static Coin fromUnsignedLong(long value) {
        return new Coin(BigInteger.valueOf(value));
    }

    public static Coin fromBytes(byte[] bytes) {
        return new Coin(new BigInteger(1, bytes));
    }

    public Coin(BigInteger value) {
        super(value);
    }
}
