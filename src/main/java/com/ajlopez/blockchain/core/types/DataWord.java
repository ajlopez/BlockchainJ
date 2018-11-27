package com.ajlopez.blockchain.core.types;

/**
 * Created by ajlopez on 27/11/2018.
 */
public class DataWord extends AbstractBytesValue {
    public static final int DATAWORD_BYTES = 32;

    public DataWord(byte[] value) {
        super(value, DATAWORD_BYTES);
    }

    @Override
    public int hashOffset() {
        return 29;
    }
}
