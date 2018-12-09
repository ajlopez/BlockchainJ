package com.ajlopez.blockchain.core.types;

import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HexUtils;

/**
 * Created by ajlopez on 27/11/2018.
 */
public class DataWord extends AbstractBytesValue {
    public static final int DATAWORD_BYTES = 32;
    public static final DataWord ZERO = new DataWord(new byte[0]);

    public static DataWord fromHexadecimalString(String value) {
        return new DataWord(HexUtils.hexStringToBytes(value));
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

    @Override
    public int hashOffset() {
        return 29;
    }
}
