package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.DataWord;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ajlopez on 20/02/2019.
 */
public class ChildMapStorage extends MapStorage {
    private final Storage parentStorage;
    private final Set<DataWord> changed = new HashSet<>();

    public ChildMapStorage(Storage parentStorage) {
        this.parentStorage = parentStorage;
    }

    @Override
    public void setValue(DataWord address, DataWord value) {
        super.setValue(address, value);

        changed.add(address);
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

        DataWord value = this.parentStorage.getValue(address);

        super.setValue(address, value);

        return value;
    }

    public void commit() {
        for (DataWord address : changed)
            this.parentStorage.setValue(address, super.getValue(address));

        changed.clear();
    }

    public boolean hasChanges() {
        return !this.changed.isEmpty();
    }
}
