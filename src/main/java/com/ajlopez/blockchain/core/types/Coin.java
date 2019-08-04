package com.ajlopez.blockchain.core.types;

import java.math.BigInteger;

/**
 * Created by ajlopez on 03/08/2019.
 */
public class Coin extends NaturalValue {
    public static final Coin ZERO = new Coin(BigInteger.ZERO);
    public static final Coin ONE = new Coin(BigInteger.ONE);
    public static final Coin TWO = Coin.fromUnsignedLong(2);
    public static final Coin TEN = new Coin(BigInteger.TEN);

    public static Coin fromUnsignedLong(long value) {
        return new Coin(BigInteger.valueOf(value));
    }

    public static Coin fromBytes(byte[] bytes) {
        return new Coin(new BigInteger(1, bytes));
    }

    public Coin(BigInteger value) {
        super(value);
    }

    public Coin add(Coin coin) {
        return new Coin(this.asBigInteger().add(coin.asBigInteger()));
    }

    public Coin subtract(Coin coin) {
        return new Coin(this.asBigInteger().subtract(coin.asBigInteger()));
    }

    public Coin multiply(long value) {
        return new Coin(this.asBigInteger().multiply(BigInteger.valueOf(value)));
    }

    public int compareTo(Coin coin) {
        return this.asBigInteger().compareTo(coin.asBigInteger());
    }

    public boolean isZero() {
        return this.asBigInteger().signum() == 0;
    }
}
