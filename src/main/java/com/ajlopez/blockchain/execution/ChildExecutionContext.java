package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.vms.eth.ChildMapStorage;
import com.ajlopez.blockchain.vms.eth.Storage;
import com.ajlopez.blockchain.vms.eth.TrieStorage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 27/11/2018.
 */
public class ChildExecutionContext extends AbstractExecutionContext {
    private final ExecutionContext parentContext;

    private final Map<Address, Storage> accountStorages = new HashMap<>();

    public ChildExecutionContext(ExecutionContext parentContext) {
        this.parentContext = parentContext;
    }

    @Override
    protected AccountState retrieveAccountState(Address address) {
        return this.parentContext.getAccountState(address).cloneState();
    }

    @Override
    protected void updateAccountState(Address address, AccountState accountState) {
        this.parentContext.setAccountState(address, accountState);
    }

    @Override
    public Storage getAccountStorage(Address address) {
        if (this.accountStorages.containsKey(address))
            return this.accountStorages.get(address);

        Storage storage = new ChildMapStorage(this.parentContext.getAccountStorage(address));

        this.accountStorages.put(address, storage);

        return storage;
    }

    @Override
    public void setAccountStorage(Address address, Storage storage) {

    }

    @Override
    public void commit() {
        for (Map.Entry<Address, Storage> entry : this.accountStorages.entrySet()) {
            ChildMapStorage storage = (ChildMapStorage)entry.getValue();
            storage.commit();
        }

        super.commit();
    }
}
