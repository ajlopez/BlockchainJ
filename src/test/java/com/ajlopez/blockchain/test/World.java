package com.ajlopez.blockchain.test;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ajlopez on 12/05/2019.
 */
public class World {
    private final AccountStore accountStore;
    private final Map<String, Address> accounts;
    private final Map<String, Block> blocks;

    private BlockChain blockChain;

    public World() {
        this.accountStore = new AccountStore(new Trie());
        this.accounts = new HashMap<>();
        this.blocks = new HashMap<>();
    }

    public Block getBlock(String name) {
        return this.blocks.get(name);
    }

    public void setBlock(String name, Block block) {
        this.blocks.put(name, block);
    }

    public Account getAccount(String name) {
        if (!this.accounts.containsKey(name))
            return null;

        return this.accountStore.getAccount(this.accounts.get(name));
    }

    public Address getAccountAddress(String name) {
        return this.accounts.get(name);
    }

    public void setAccount(String name, Account account) {
        Address address = FactoryHelper.createRandomAddress();
        this.accounts.put(name, address);
        this.accountStore.putAccount(address, account);
    }

    public BlockChain getBlockChain() {
        if (this.blockChain == null)
            this.blockChain = FactoryHelper.createBlockChainWithGenesis(this.accountStore);

        return this.blockChain;
    }
}

