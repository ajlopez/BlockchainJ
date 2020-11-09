package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.vms.eth.Storage;

import java.io.IOException;

/**
 * Created by ajlopez on 30/04/2019.
 */
public interface AccountProvider {
    byte[] getCode(Address address) throws IOException;

    long getCodeLength(Address address) throws IOException;

    Hash getCodeHash(Address address) throws IOException;

    Coin getBalance(Address address) throws IOException;

    long getNonce(Address address) throws IOException;

    Storage getAccountStorage(Address address) throws IOException;
}
