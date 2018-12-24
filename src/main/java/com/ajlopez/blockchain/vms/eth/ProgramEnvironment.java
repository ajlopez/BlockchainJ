package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.DataWord;

/**
 * Created by ajlopez on 14/12/2018.
 */
public class ProgramEnvironment {
    private final Address address;
    private final Address origin;
    private final Address caller;
    private final DataWord value;
    private final BlockData blockData;

    public ProgramEnvironment(Address address, Address origin, Address caller, DataWord value, BlockData blockData) {
        this.address = address;
        this.origin = origin;
        this.caller = caller;
        this.value = value;
        this.blockData = blockData;
    }

    public Address getAddress() { return this.address; }

    public Address getOrigin() { return this.origin; }

    public Address getCaller() { return this.caller; }

    public DataWord getValue() { return this.value; }

    public long getNumber() { return this.blockData.getNumber(); }

    public DataWord getDifficulty() { return this.blockData.getDifficulty(); }

    public long getTimestamp() { return this.blockData.getTimestamp(); }

    public Address getCoinbase() { return this.blockData.getCoinbase(); }

}
