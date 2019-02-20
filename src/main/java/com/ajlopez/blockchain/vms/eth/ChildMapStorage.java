package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;

/**
 * Created by ajlopez on 20/02/2019.
 */
public class ChildMapStorage extends MapStorage {
    private final Storage parentStorage;

    public ChildMapStorage(Storage parentStorage) {
        this.parentStorage = parentStorage;
    }

    @Override
    public boolean hasValue(DataWord address) {
        if (super.hasValue(address))
            return true;

        return parentStorage.hasValue(address);
    }

    @Override
    public DataWord getValue(DataWord address) {
        if (super.hasValue(address))
            return super.getValue(address);

        return this.parentStorage.getValue(address);
    }
}
