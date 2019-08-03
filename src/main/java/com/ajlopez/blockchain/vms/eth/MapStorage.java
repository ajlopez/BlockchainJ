package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 09/12/2018.
 */

public class MapStorage implements Storage {
    private Map<DataWord, DataWord> values = new HashMap<>();

    @Override
    public boolean hasValue(DataWord address) {
        return this.values.containsKey(address);
    }

    @Override
    public DataWord getValue(DataWord address) {
        if (this.hasValue(address))
            return this.values.get(address);

        return DataWord.ZERO;
    }

    @Override
    public void setValue(DataWord address, DataWord value) {
        this.values.put(address, value);
    }

    @Override
    public void commit() { }
}
