package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.TrieStore;
import com.ajlopez.blockchain.vms.eth.ChildMapStorage;
import com.ajlopez.blockchain.vms.eth.Storage;
import com.ajlopez.blockchain.vms.eth.TrieStorage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 26/11/2018.
 */
public class TopExecutionContext extends AbstractExecutionContext {
    private final AccountStore accountStore;
    private final TrieStore storageStore;

    private final Map<Address, Storage> accountStorages = new HashMap<>();

    public TopExecutionContext(AccountStore accountStore, TrieStore storageStore)
    {
        this.accountStore = accountStore;
        this.storageStore = storageStore;
    }

    @Override
    protected AccountState retrieveAccountState(Address address) {
        return AccountState.fromAccount(this.accountStore.getAccount(address));
    }

    @Override
    protected void updateAccountState(Address address, AccountState accountState) {
        this.accountStore.putAccount(address, accountState.toAccount());
    }

    @Override
    public Storage getAccountStorage(Address address) {
        if (this.accountStorages.containsKey(address))
            return this.accountStorages.get(address);

        Storage storage = new TrieStorage(this.storageStore.retrieve(this.getStorageHash(address)));

        this.accountStorages.put(address, storage);

        return storage;
    }

    @Override
    public void commit() {
        for (Map.Entry<Address, Storage> entry : this.accountStorages.entrySet()) {
            TrieStorage storage = (TrieStorage)entry.getValue();
            storage.commit();
            this.getAccountState(entry.getKey()).setStorageHash(storage.getRootHash());
        }

        super.commit();
    }
}
