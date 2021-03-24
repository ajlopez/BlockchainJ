package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.types.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 24/03/2021.
 */
public class Wallet {
    private final List<Address> addresses = new ArrayList<>();

    public void addAddress(Address address) {
        this.addresses.add(address);
    }

    public List<Address> getAddresses() {
        return this.addresses;
    }
}
