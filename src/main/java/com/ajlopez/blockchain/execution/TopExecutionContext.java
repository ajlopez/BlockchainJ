package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.TrieStore;

/**
 * Created by ajlopez on 26/11/2018.
 */
public class TopExecutionContext extends AbstractExecutionContext {
    private final AccountStore accountStore;
    private final TrieStore storageStore;

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
}
