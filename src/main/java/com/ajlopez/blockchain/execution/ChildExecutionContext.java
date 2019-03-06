package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;

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
        this.parentContext.putAccountState(address, accountState);
    }
}
