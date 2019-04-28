package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.vms.eth.ChildMapStorage;
import com.ajlopez.blockchain.vms.eth.Storage;

/**
 * Created by ajlopez on 27/11/2018.
 */
public class ChildExecutionContext extends AbstractExecutionContext {
    private final ExecutionContext parentContext;

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
    public Storage retrieveAccountStorage(Address address) {
        return new ChildMapStorage(this.parentContext.getAccountStorage(address));
    }

    @Override
    public byte[] getCode(Address address) {
        // TODO implement in child context
        return parentContext.getCode(address);
    }
}
