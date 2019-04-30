package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;

/**
 * Created by ajlopez on 30/04/2019.
 */
public interface CodeProvider {
    byte[] getCode(Address address);
}
