package com.ajlopez.blockchain.vms.eth;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.DataWord;

import java.util.ArrayList;
import java.util.Collections;
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
        // TODO review if null or empty array normalization
        this.data = data != null && data.length == 0 ? null : data;
        this.topics = topics == null ? Collections.EMPTY_LIST : Collections.unmodifiableList(new ArrayList<>(topics));
    }

    public Address getAddress() { return this.address; }

    public byte[] getData() { return this.data; }

    public List<DataWord> getTopics() { return this.topics; }
}
