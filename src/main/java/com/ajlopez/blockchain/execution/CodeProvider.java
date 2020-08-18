package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Hash;

import java.io.IOException;

/**
 * Created by ajlopez on 30/04/2019.
 */
public interface CodeProvider {
    byte[] getCode(Address address) throws IOException;

    long getCodeLength(Address address) throws IOException;

    Hash getCodeHash(Address address) throws IOException;
}
