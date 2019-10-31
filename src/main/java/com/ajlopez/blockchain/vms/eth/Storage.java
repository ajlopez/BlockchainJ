package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;

import java.io.IOException;

/**
 * Created by ajlopez on 09/12/2018.
 */

public interface Storage {
    boolean hasValue(DataWord address) throws IOException;

    void setValue(DataWord address, DataWord value);

    DataWord getValue(DataWord address) throws IOException;

    void commit() throws IOException;
}
