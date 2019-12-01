package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.vms.eth.Storage;

import java.io.IOException;

/**
 * Created by ajlopez on 16/02/2018.
 */
public interface ExecutionContext extends CodeProvider {
    void transfer(Address senderAddress, Address receiverAddress, Coin amount) throws IOException;

    void incrementNonce(Address address) throws IOException;

    Coin getBalance(Address address) throws IOException;

    long getNonce(Address address) throws IOException;

    Hash getCodeHash(Address address) throws IOException;

    void setCodeHash(Address address, Hash codeHash) throws IOException;

    void commit() throws IOException;

    void rollback();

    AccountState getAccountState(Address address) throws IOException;

    void setAccountState(Address address, AccountState accountState);

    Storage getAccountStorage(Address address) throws IOException;

    Storage retrieveAccountStorage(Address address) throws IOException;

    void setCode(Address address, byte[] code) throws IOException;
}
