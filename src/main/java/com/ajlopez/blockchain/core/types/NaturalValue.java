package com.ajlopez.blockchain.core.types;

import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HexUtils;

import java.math.BigInteger;

/**
 * Created by ajlopez on 03/08/2019.
 */
public class NaturalValue {
    public static final int NATURAL_VALUE_BYTES = 32;

    private final BigInteger value;

    public NaturalValue(BigInteger value) {
        if (value.signum() < 0)
            throw new ArithmeticException("Natural value cannot be negative");

        byte[] binat = value.toByteArray();
        int nzeros = 0;

        for (int k = 0; k < binat.length; k++)
            if (binat[k] == 0)
                nzeros++;
            else
                break;

        if (value.toByteArray().length - nzeros > NATURAL_VALUE_BYTES)
            throw new ArithmeticException("Natural value is too big");

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

    public byte[] toBytes() {
        return ByteUtils.normalizedBytes(this.value.toByteArray());
    }

    @Override
    public String toString() {
        return HexUtils.bytesToHexString(this.toBytes(), true, true);
    }

    public DataWord toDataWord() {
        byte[] bytes = this.toBytes();

        return DataWord.fromBytes(bytes, 0, bytes.length);
    }
}
