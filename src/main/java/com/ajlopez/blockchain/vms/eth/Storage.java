package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;

/**
 * Created by ajlopez on 09/12/2018.
 */

public interface Storage {
    void setValue(DataWord address, DataWord value);

    DataWord getValue(DataWord address);
}
