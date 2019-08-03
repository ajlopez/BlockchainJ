package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.vms.eth.Storage;

/**
 * Created by ajlopez on 16/02/2018.
 */
public interface ExecutionContext extends CodeProvider {
    void transfer(Address senderAddress, Address receiverAddress, Coin amount);

    void incrementNonce(Address address);

    Coin getBalance(Address address);

    long getNonce(Address address);

    Hash getCodeHash(Address address);

    void setCodeHash(Address address, Hash codeHash);

    void commit();

    void rollback();

    AccountState getAccountState(Address address);

    void setAccountState(Address address, AccountState accountState);

    Storage getAccountStorage(Address address);

    Storage retrieveAccountStorage(Address address);
}
