package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.vms.eth.Storage;
import com.ajlopez.blockchain.vms.eth.TrieStorage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 27/11/2018.
 */
public abstract class AbstractExecutionContext implements ExecutionContext {
    private final Map<Address, AccountState> accountStates = new HashMap<>();
    private final Map<Address, Storage> accountStorages = new HashMap<>();

    @Override
    public void transfer(Address senderAddress, Address receiverAddress, Coin amount) throws IOException {
        AccountState sender = this.getAccountState(senderAddress);
        AccountState receiver = this.getAccountState(receiverAddress);

        sender.subtractFromBalance(amount);
        receiver.addToBalance(amount);
    }

    @Override
    public void incrementNonce(Address address) throws IOException {
        AccountState accountState = this.getAccountState(address);

        accountState.incrementNonce();
    }

    @Override
    public Coin getBalance(Address address) throws IOException {
        return this.getAccountState(address).getBalance();
    }

    @Override
    public long getNonce(Address address) throws IOException {
        return this.getAccountState(address).getNonce();
    }

    @Override
    public Hash getCodeHash(Address address) throws IOException { return this.getAccountState(address).getCodeHash(); }

    @Override
    public void setCodeHash(Address address, Hash codeHash) throws IOException { this.getAccountState(address).setCodeHash(codeHash); }

    @Override
    public void commit() throws IOException {
        for (Map.Entry<Address, Storage> entry : this.accountStorages.entrySet()) {
            Storage storage = entry.getValue();
            storage.commit();

            // TODO Improve
            if (storage instanceof TrieStorage) {
                Address address = entry.getKey();
                this.getAccountState(address).setStorageHash(((TrieStorage)storage).getRootHash());
            }
        }

        for (Map.Entry<Address, AccountState> entry : this.accountStates.entrySet()) {
            Address address = entry.getKey();
            AccountState accountState = entry.getValue();

            if (!accountState.wasChanged())
                continue;

            this.updateAccountState(address, accountState);
        }

        this.accountStorages.clear();
        this.accountStates.clear();
    }

    @Override
    public void rollback() {
        this.accountStorages.clear();
        this.accountStates.clear();
    }

    @Override
    public AccountState getAccountState(Address address) throws IOException {
        if (this.accountStates.containsKey(address))
            return this.accountStates.get(address);

        AccountState accountState = this.retrieveAccountState(address);

        this.setAccountState(address, accountState);

        return accountState;
    }

    @Override
    public void setAccountState(Address address, AccountState accountState) {
        this.accountStates.put(address, accountState);
    }

    @Override
    public Storage getAccountStorage(Address address) throws IOException {
        if (this.accountStorages.containsKey(address))
            return this.accountStorages.get(address);

        Storage storage = this.retrieveAccountStorage(address);

        this.accountStorages.put(address, storage);

        return storage;
    }

    abstract AccountState retrieveAccountState(Address address) throws IOException;

    abstract void updateAccountState(Address address, AccountState accountState);

    abstract public Storage retrieveAccountStorage(Address address) throws IOException;
}
