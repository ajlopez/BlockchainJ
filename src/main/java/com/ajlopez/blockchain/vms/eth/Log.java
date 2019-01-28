package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.DataWord;

import java.util.List;

/**
 * Created by ajlopez on 28/01/2019.
 */
public class Log {
    private Address address;
    private byte[] data;
    private List<DataWord> topics;

    public Log(Address address, byte[] data, List<DataWord> topics) {
        this.address = address;
        this.data = data;
        this.topics = topics;
    }

    public Address getAddress() { return this.address; }

    public byte[] getData() { return this.data; }

    public List<DataWord> getTopics() { return this.topics; }
}
