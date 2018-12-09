package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 09/12/2018.
 */

public class MapStorage implements Storage {
    private Map<DataWord, DataWord> values = new HashMap<>();

    public DataWord getValue(DataWord address) {
        if (values.containsKey(address))
            return values.get(address);

        return DataWord.ZERO;
    }

    public void setValue(DataWord address, DataWord value) {
        if (value.equals(DataWord.ZERO))
            values.remove(address);
        else
            values.put(address, value);
    }
}
