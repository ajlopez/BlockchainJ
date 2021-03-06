package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.vms.eth.Storage;

import java.io.IOException;

/**
 * Created by ajlopez on 16/02/2018.
 */
public interface ExecutionContext extends AccountProvider {
    void transfer(Address senderAddress, Address receiverAddress, Coin amount) throws IOException;

    void incrementNonce(Address address) throws IOException;

    void commit() throws IOException;

    void rollback();

    void setCode(Address address, byte[] code) throws IOException;

    void setCodeData(Address address, long codeLength, Hash codeHash) throws IOException;

    ExecutionContext createChildExecutionContext();
}
