package com.ajlopez.blockchain.core.types;

import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HexUtils;

import java.math.BigInteger;

/**
 * Created by ajlopez on 27/11/2018.
 */
public class DataWord extends AbstractBytesValue implements Comparable<DataWord> {
    public static final int DATAWORD_BYTES = 32;
    private static final int MAX_POW = 256;
    public static final BigInteger _2_256 = BigInteger.valueOf(2).pow(MAX_POW);

    public static final DataWord ZERO = new DataWord(new byte[0]);
    public static final DataWord ONE = new DataWord(new byte[] { 0x01 });
    public static final DataWord TWO = new DataWord(new byte[] { 0x02 });
    public static final DataWord MAX = fromBigInteger(_2_256.subtract(BigInteger.ONE));

    public static DataWord fromUnsignedInteger(int value) {
        return new DataWord(ByteUtils.unsignedIntegerToBytes(value));
    }

    public static DataWord fromUnsignedLong(long value) {
        return new DataWord(ByteUtils.unsignedLongToBytes(value));
    }

    public static DataWord fromHexadecimalString(String value) {
        return new DataWord(HexUtils.hexStringToBytes(value));
    }

    public static DataWord fromBigInteger(BigInteger value) {
        byte[] bytes = value.toByteArray();
        return fromBytes(bytes, 0, bytes.length);
    }

    public static DataWord fromCoin(Coin coin) {
        byte[] bytes = coin.toBytes();
        return fromBytes(bytes, 0, bytes.length);
    }

    public static DataWord fromBytes(byte[] bytes) {
        return fromBytes(bytes, 0, bytes.length);
    }

    public static DataWord fromBytes(byte[] bytes, int offset, int length) {
        byte[] newbytes = new byte[DATAWORD_BYTES];

        while (length > 0 && bytes[offset] == 0) {
            offset++;
            length--;
        }

        if (length > DATAWORD_BYTES)
            throw new IllegalArgumentException("Too large byte array");

        System.arraycopy(bytes, offset, newbytes, DATAWORD_BYTES - length, length);

        return new DataWord(newbytes);
    }

    public static DataWord fromBytesToLeft(byte[] bytes, int offset, int length) {
        byte[] newbytes = new byte[DATAWORD_BYTES];

        System.arraycopy(bytes, offset, newbytes, 0, length);

        return new DataWord(newbytes);
    }

    public static DataWord fromAddress(Address address) {
        byte[] bytes = address.getBytes();

        return fromBytes(bytes, 0, bytes.length);
    }

    public DataWord(byte[] value) {
        super(value, DATAWORD_BYTES);
    }

    public DataWord(byte[] value, boolean signed) {
        super(value, DATAWORD_BYTES, signed);
    }

    public byte[] toNormalizedBytes() {
        return ByteUtils.normalizedBytes(this.bytes);
    }

    public String toNormalizedString() {
        return HexUtils.bytesToHexString(this.toNormalizedBytes(), true);
    }

    public Address toAddress() {
        byte[] newbytes = new byte[Address.ADDRESS_BYTES];

        System.arraycopy(this.bytes, DATAWORD_BYTES - Address.ADDRESS_BYTES, newbytes, 0, Address.ADDRESS_BYTES);

        return new Address(newbytes);
    }

    public int asUnsignedInteger() {
        return ByteUtils.bytesToUnsignedInteger(this.bytes, DataWord.DATAWORD_BYTES - Integer.BYTES);
    }

    public long asUnsignedLong() {
        return ByteUtils.bytesToUnsignedLong(this.bytes, DataWord.DATAWORD_BYTES - Long.BYTES);
    }

    // From : http://stackoverflow.com/a/24023466/459349
    public DataWord add(DataWord word) {
        byte[] newbytes = new byte[DATAWORD_BYTES];

        for (int k = DATAWORD_BYTES - 1, overflow = 0; k >= 0; k--) {
            int v = (this.bytes[k] & 0xff) + (word.bytes[k] & 0xff) + overflow;
            newbytes[k] = (byte) v;
            overflow = v >>> 8;
        }

        return new DataWord(newbytes);
    }

    public DataWord sub(DataWord word) {
        byte[] newbytes = new byte[DATAWORD_BYTES];

        for (int k = DATAWORD_BYTES - 1, overflow = 1; k >= 0; k--) {
            int v = (this.bytes[k] & 0xff) + (((byte)~word.bytes[k]) & 0xff) + overflow;
            newbytes[k] = (byte) v;
            overflow = v >>> 8;
        }

        return new DataWord(newbytes);
    }

    public DataWord mul(DataWord word) {
        BigInteger value1 = new BigInteger(1, this.bytes);
        BigInteger value2 = new BigInteger(1, word.bytes);

        byte[] newbytes = value1.multiply(value2).toByteArray();

        if (newbytes.length > DATAWORD_BYTES)
            return DataWord.fromBytes(newbytes, newbytes.length - DATAWORD_BYTES, DATAWORD_BYTES);

        return new DataWord(newbytes);
    }

    public DataWord div(DataWord word) {
        BigInteger value1 = new BigInteger(1, this.bytes);
        BigInteger value2 = new BigInteger(1, word.bytes);

        byte[] newbytes = value1.divide(value2).toByteArray();

        if (newbytes.length > DATAWORD_BYTES)
            return DataWord.fromBytes(newbytes, newbytes.length - DATAWORD_BYTES, DATAWORD_BYTES);

        return new DataWord(newbytes);
    }

    public DataWord sdiv(DataWord word) {
        BigInteger value1 = new BigInteger(this.bytes);
        BigInteger value2 = new BigInteger(word.bytes);

        byte[] newbytes = value1.divide(value2).toByteArray();

        if (newbytes.length > DATAWORD_BYTES)
            return DataWord.fromBytes(newbytes, newbytes.length - DATAWORD_BYTES, DATAWORD_BYTES);

        return new DataWord(newbytes, true);
    }

    public DataWord mod(DataWord word) {
        BigInteger value1 = new BigInteger(1, this.bytes);
        BigInteger value2 = new BigInteger(1, word.bytes);

        byte[] newbytes = value1.mod(value2).toByteArray();

        if (newbytes.length > DATAWORD_BYTES)
            return DataWord.fromBytes(newbytes, newbytes.length - DATAWORD_BYTES, DATAWORD_BYTES);

        return new DataWord(newbytes);
    }

    public DataWord smod(DataWord word) {
        BigInteger value1 = new BigInteger(this.bytes);
        BigInteger value2 = new BigInteger(word.bytes);

        BigInteger result = value1.abs().mod(value2.abs());

        if (this.bytes[0] < 0)
            result = result.negate();

        byte[] newbytes = result.toByteArray();

        if (newbytes.length > DATAWORD_BYTES)
            return DataWord.fromBytes(newbytes, newbytes.length - DATAWORD_BYTES, DATAWORD_BYTES);

        return new DataWord(newbytes, true);
    }

    public DataWord exp(DataWord word) {
        BigInteger value1 = new BigInteger(1, this.bytes);
        BigInteger value2 = new BigInteger(1, word.bytes);

        byte[] newbytes = value1.modPow(value2, _2_256).toByteArray();

        if (newbytes.length > DATAWORD_BYTES)
            return DataWord.fromBytes(newbytes, newbytes.length - DATAWORD_BYTES, DATAWORD_BYTES);

        return new DataWord(newbytes);
    }

    public DataWord or(DataWord word) {
        byte[] newbytes = new byte[DATAWORD_BYTES];

        for (int k = DATAWORD_BYTES - 1; k >= 0; k--) {
            int v = this.bytes[k] | word.bytes[k];
            newbytes[k] = (byte) v;
        }

        return new DataWord(newbytes);
    }

    public DataWord and(DataWord word) {
        byte[] newbytes = new byte[DATAWORD_BYTES];

        for (int k = DATAWORD_BYTES - 1; k >= 0; k--) {
            int v = this.bytes[k] & word.bytes[k];
            newbytes[k] = (byte) v;
        }

        return new DataWord(newbytes);
    }

    public DataWord xor(DataWord word) {
        byte[] newbytes = new byte[DATAWORD_BYTES];

        for (int k = DATAWORD_BYTES - 1; k >= 0; k--) {
            int v = this.bytes[k] ^ word.bytes[k];
            newbytes[k] = (byte) v;
        }

        return new DataWord(newbytes);
    }

    public DataWord not() {
        byte[] newbytes = new byte[DATAWORD_BYTES];

        for (int k = DATAWORD_BYTES - 1; k >= 0; k--)
            newbytes[k] = (byte)(this.bytes[k] ^ 0xff);

        return new DataWord(newbytes);
    }

    public DataWord negate() {
        return this.not().add(DataWord.ONE);
    }

    public boolean isUnsignedInteger() {
        for (int k = 0; k < DATAWORD_BYTES - Integer.BYTES; k++)
            if (this.bytes[k] != 0)
                return false;

        if ((this.bytes[DATAWORD_BYTES - Integer.BYTES] & 0x80) != 0)
            return false;

        return true;
    }

    public boolean isUnsignedLong() {
        for (int k = 0; k < DATAWORD_BYTES - Long.BYTES; k++)
            if (this.bytes[k] != 0)
                return false;

        if ((this.bytes[DATAWORD_BYTES - Long.BYTES] & 0x80) != 0)
            return false;

        return true;
    }

    public boolean isZero() {
        int nbytes = this.bytes.length;

        for (int k = 0; k < nbytes; k++)
            if (this.bytes[k] != 0)
                return false;

        return true;
    }

    public boolean isNegative() {
        return (this.bytes[0] & 0x80) != 0;
    }

    @Override
    public int compareTo(DataWord word) {
        for (int k = 0; k < DATAWORD_BYTES; k++) {
            int r = Integer.compare(this.bytes[k] & 0xff, word.bytes[k] & 0xff);

            if (r != 0)
                return r;
        }

        return 0;
    }

    public int compareTo(Hash hash) {
        for (int k = 0; k < DATAWORD_BYTES; k++) {
            int r = Integer.compare(this.bytes[k] & 0xff, hash.bytes[k] & 0xff);

            if (r != 0)
                return r;
        }

        return 0;
    }

    public int compareToSigned(DataWord word) {
        if (this.isNegative()) {
            if (!word.isNegative())
                return -1;

            return -this.negate().compareTo(word.negate());
        }

        if (word.isNegative())
            return 1;

        return compareTo(word);
    }

    public DataWord shiftLeft(DataWord word) {
        if (!word.isUnsignedInteger())
            return DataWord.ZERO;

        int nbits = word.asUnsignedInteger();

        if (nbits >= MAX_POW)
            return DataWord.ZERO;

        byte[] bytes = ByteUtils.shiftLeft(this.getBytes(), nbits);

        return new DataWord(bytes);
    }

    public DataWord shiftRight(DataWord word) {
        if (!word.isUnsignedInteger())
            return DataWord.ZERO;

        int nbits = word.asUnsignedInteger();

        if (nbits >= MAX_POW)
            return DataWord.ZERO;

        byte[] bytes = ByteUtils.shiftRight(this.getBytes(), nbits);

        return new DataWord(bytes);
    }

    public DataWord shiftArithmeticRight(DataWord word) {
        if (!word.isUnsignedInteger())
            return DataWord.ZERO;

        int nbits = word.asUnsignedInteger();

        if (nbits >= MAX_POW)
            if ((this.getBytes()[0] & 0x80) != 0)
                return DataWord.ZERO.sub(DataWord.ONE);
            else
                return DataWord.ZERO;

        byte[] bytes = ByteUtils.shiftArithmeticRight(this.getBytes(), nbits);

        return new DataWord(bytes);
    }
}
