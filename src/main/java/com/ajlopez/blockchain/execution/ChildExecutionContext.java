package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.vms.eth.ChildMapStorage;
import com.ajlopez.blockchain.vms.eth.Storage;

import java.io.IOException;

/**
 * Created by ajlopez on 27/11/2018.
 */
public class ChildExecutionContext extends AbstractExecutionContext {
    private final AbstractExecutionContext parentContext;

    public ChildExecutionContext(AbstractExecutionContext parentContext) {
        this.parentContext = parentContext;
    }

    @Override
    protected AccountState retrieveAccountState(Address address) throws IOException {
        return this.parentContext.getAccountState(address).cloneState();
    }

    @Override
    protected void updateAccountState(Address address, AccountState accountState) {
        this.parentContext.setAccountState(address, accountState);
    }

    @Override
    public Storage retrieveAccountStorage(Address address) throws IOException {
        return new ChildMapStorage(this.parentContext.getAccountStorage(address));
    }

    @Override
    public void updateCode(Hash hash, byte[] code) throws IOException {
        this.parentContext.setCode(hash, code);
    }

    @Override
    public byte[] retrieveCode(Hash hash) throws IOException {
        return this.parentContext.getCode(hash);
    }
}
