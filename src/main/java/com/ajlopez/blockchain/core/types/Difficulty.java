package com.ajlopez.blockchain.core.types;

import java.math.BigInteger;

/**
 * Created by ajlopez on 24/08/2019.
 */
public class Difficulty extends NaturalValue {
    public static final Difficulty ZERO = new Difficulty(BigInteger.ZERO);
    public static final Difficulty ONE = new Difficulty(BigInteger.ONE);
    public static final Difficulty TWO = Difficulty.fromUnsignedLong(2);
    public static final Difficulty THREE = Difficulty.fromUnsignedLong(3);
    public static final Difficulty TEN = new Difficulty(BigInteger.TEN);

    private static final BigInteger MAX = BigInteger.valueOf(2).pow(256);

    public static Difficulty fromUnsignedLong(long value) {
        return new Difficulty(BigInteger.valueOf(value));
    }

    public static Difficulty fromBytes(byte[] bytes) {
        return new Difficulty(new BigInteger(1, bytes));
    }

    public Difficulty(BigInteger value) {
        super(value);
    }

    public int compareTo(Difficulty difficulty) {
        return this.asBigInteger().compareTo(difficulty.asBigInteger());
    }

    public boolean isZero() {
        return this.asBigInteger().signum() == 0;
    }

    public Difficulty add(Difficulty difficulty) {
        DataWord diff1 = DataWord.fromBigInteger(this.asBigInteger());
        DataWord diff2 = DataWord.fromBigInteger(difficulty.asBigInteger());

        DataWord result = diff1.add(diff2);

        return Difficulty.fromBytes(result.getBytes());
    }

    public DataWord toTarget() {
        return DataWord.fromBigInteger(MAX.divide(this.asBigInteger()));
    }
}
