package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.DataWord;

/**
 * Created by ajlopez on 24/12/2018.
 */
public class MessageData {
    private final Address address;
    private final Address origin;
    private final Address caller;
    private final DataWord value;
    private final long gas;

    public MessageData(Address address, Address origin, Address caller, DataWord value, long gas) {
        this.address = address;
        this.origin = origin;
        this.caller = caller;
        this.value = value;
        this.gas = gas;
    }

    public Address getAddress() { return this.address; }

    public Address getOrigin() { return this.origin; }

    public Address getCaller() { return this.caller; }

    public DataWord getValue() { return this.value; }

    public long getGas() { return this.gas; }
}