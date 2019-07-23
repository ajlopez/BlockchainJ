package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.CodeStore;
import com.ajlopez.blockchain.vms.eth.Storage;
import com.ajlopez.blockchain.vms.eth.TrieStorageProvider;

/**
 * Created by ajlopez on 26/11/2018.
 */
public class TopExecutionContext extends AbstractExecutionContext {
    private final AccountStore accountStore;
    private final TrieStorageProvider trieStorageProvider;
    private final CodeStore codeStore;

    public TopExecutionContext(AccountStore accountStore, TrieStorageProvider trieStorageProvider, CodeStore codeStore)
    {
        this.accountStore = accountStore;
        this.trieStorageProvider = trieStorageProvider;
        this.codeStore = codeStore;
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
    public Storage retrieveAccountStorage(Address address) {
        AccountState accountState = this.getAccountState(address);
        Storage storage = this.trieStorageProvider.retrieve(accountState);

        return storage;
    }

    @Override
    public byte[] getCode(Address address) {
        AccountState accountState = this.getAccountState(address);

        if (accountState == null)
            return null;

        Hash codeHash = accountState.getCodeHash();

        if (codeHash == null)
            return null;

        return this.codeStore.getCode(codeHash);
    }

    @Override
    public void commit() {
        super.commit();
        // TODO save other stores??
        this.accountStore.save();
    }
}
