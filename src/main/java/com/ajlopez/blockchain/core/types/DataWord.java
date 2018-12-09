package com.ajlopez.blockchain.core.types;

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

    @Override
    public int hashOffset() {
        return 29;
    }
}
