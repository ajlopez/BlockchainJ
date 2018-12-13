package com.ajlopez.blockchain.core.types;

import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HexUtils;

/**
 * Created by ajlopez on 27/11/2018.
 */
public class DataWord extends AbstractBytesValue implements Comparable<DataWord> {
    public static final int DATAWORD_BYTES = 32;

    public static final DataWord ZERO = new DataWord(new byte[0]);
    public static final DataWord ONE = new DataWord(new byte[] { 0x01 });

    public static DataWord fromUnsignedInteger(int value) {
        return new DataWord(ByteUtils.unsignedIntegerToBytes(value));
    }

    public static DataWord fromHexadecimalString(String value) {
        return new DataWord(HexUtils.hexStringToBytes(value));
    }

    public static DataWord fromBytes(byte[] bytes, int offset, int length) {
        byte[] newbytes = new byte[DATAWORD_BYTES];

        System.arraycopy(bytes, offset, newbytes, DATAWORD_BYTES - length, length);

        return new DataWord(newbytes);
    }

    public DataWord(byte[] value) {
        super(value, DATAWORD_BYTES);
    }

    public byte[] toNormalizedBytes() {
        return ByteUtils.normalizedBytes(this.bytes);
    }

    public String toNormalizedString() {
        return HexUtils.bytesToHexString(this.toNormalizedBytes(), true);
    }

    public int asUnsignedInteger() {
        return ByteUtils.bytesToUnsignedInteger(this.bytes, DataWord.DATAWORD_BYTES - Integer.BYTES);
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

    public DataWord or(DataWord word) {
        byte[] newbytes = new byte[DATAWORD_BYTES];

        for (int k = DATAWORD_BYTES - 1; k >= 0; k--) {
            int v = this.bytes[k] |word.bytes[k];
            newbytes[k] = (byte) v;
        }

        return new DataWord(newbytes);
    }

    @Override
    public int hashOffset() {
        return 29;
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
}
