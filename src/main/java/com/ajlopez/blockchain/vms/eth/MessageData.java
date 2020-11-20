package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;

/**
 * Created by ajlopez on 24/12/2018.
 */
public class MessageData {
    private final Address address;
    private final Address origin;
    private final Address caller;
    private final Coin value;
    private final long gas;
    private final Coin gasPrice;
    private final byte[] data;
    private final boolean readOnly;
    private final int outputDataOffset;
    private final int outputDataSize;

    public MessageData(Address address, Address origin, Address caller, Coin value, long gas, Coin gasPrice, byte[] data, int outputDataOffset, int outputDataSize, boolean readOnly) {
        this.address = address;
        this.origin = origin;
        this.caller = caller;
        this.value = value;
        this.gas = gas;
        this.gasPrice = gasPrice;
        this.data = data;
        this.outputDataOffset = outputDataOffset;
        this.outputDataSize = outputDataSize;
        this.readOnly = readOnly;
    }

    public Address getAddress() { return this.address; }

    public Address getOrigin() { return this.origin; }

    public Address getCaller() { return this.caller; }

    public Coin getValue() { return this.value; }

    public long getGas() { return this.gas; }

    public Coin getGasPrice() { return this.gasPrice; }

    public byte[] getData() { return this.data; }

    public int getOutputDataOffset() {
        return this.outputDataOffset;
    }

    public int getOutputDataSize() {
        return this.outputDataSize;
    }

    public boolean isReadOnly() { return this.readOnly; }
}
