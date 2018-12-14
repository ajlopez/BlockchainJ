package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.Address;

/**
 * Created by ajlopez on 14/12/2018.
 */
public class ProgramEnvironment {
    private final Address address;
    private final Address origin;
    private final Address caller;

    public ProgramEnvironment(Address address, Address origin, Address caller) {
        this.address = address;
        this.origin = origin;
        this.caller = caller;
    }

    public Address getAddress() { return this.address; }

    public Address getOrigin() { return this.origin; }

    public Address getCaller() { return this.caller; }
}
