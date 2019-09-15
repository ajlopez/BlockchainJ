package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.AccountStoreProvider;

/**
 * Created by ajlopez on 15/09/2019.
 */
public class AccountsProvider {
    private final BlocksProvider blocksProvider;
    private final AccountStoreProvider accountStoreProvider;

    public AccountsProvider(BlocksProvider blocksProvider, AccountStoreProvider accountStoreProvider) {
        this.blocksProvider = blocksProvider;
        this.accountStoreProvider = accountStoreProvider;
    }

    public Account getAccount(Address address, String blockId) throws JsonRpcException {
        Block block = this.blocksProvider.getBlock(blockId);
        AccountStore accountStore = this.accountStoreProvider.retrieve(block.getStateRootHash());

        return accountStore.getAccount(address);
    }
}
