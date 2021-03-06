package com.ajlopez.blockchain.test;

import com.ajlopez.blockchain.bc.*;
import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.processors.BlockProcessor;
import com.ajlopez.blockchain.processors.OrphanBlocks;
import com.ajlopez.blockchain.processors.TransactionPool;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.*;
import com.ajlopez.blockchain.test.simples.SimpleBlockValidator;
import com.ajlopez.blockchain.test.utils.FactoryHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ajlopez on 12/05/2019.
 */
public class World {
    private final ObjectContext objectContext;
    private final Stores stores;
    private final AccountStore accountStore;
    private final CodeStore codeStore;

    private final Map<String, Address> accounts = new HashMap<>();
    private final Map<String, Block> blocks = new HashMap<>();
    private final Map<String, BlockHeader> blockHeaders = new HashMap<>();
    private final Map<String, Transaction> transactions = new HashMap<>();

    private BlockChain blockChain;
    private BlockProcessor blockProcessor;

    public World() throws IOException {
        this.objectContext = new ObjectContext(new MemoryKeyValueStores());
        this.stores = this.objectContext.getStores();
        this.accountStore = new AccountStore(this.stores.getAccountTrieStore().retrieve(Trie.EMPTY_TRIE_HASH));
        this.codeStore = this.stores.getCodeStore();
    }

    public Stores getStores() { return this.stores; }

    public BlockStore getBlockStore() {
        return this.stores.getBlockStore();
    }

    public BlocksInformationStore getBlocksInformationStore() {
        return this.stores.getBlocksInformationStore();
    }

    public Transaction getTransaction(String name) {
        return this.transactions.get(name);
    }

    public List<Transaction> getTransactions(List<String> names) {
        List<Transaction> result = new ArrayList<>();

        for (String name : names) {
            Transaction transaction = this.getTransaction(name);

            if (transaction != null)
                result.add(transaction);
        }

        return result;
    }

    public void setTransaction(String name, Transaction transaction) {
        this.transactions.put(name, transaction);
    }

    public BlockHeader getBlockHeader(String name) throws IOException {
        BlockHeader blockHeader = this.blockHeaders.get(name);

        if (blockHeader != null)
            return blockHeader;

        return blockHeader;
    }

    public void setBlockHeader(String name, BlockHeader blockHeader) {
        this.blockHeaders.put(name, blockHeader);
    }

    public Block getBlock(String name) throws IOException {
        Block block = this.blocks.get(name);

        if (block != null)
            return block;

        if ("genesis".equals(name)) {
            block = this.getBlockChain().getBlockByNumber(0);
            this.blocks.put("genesis", block);
        }

        return block;
    }

    public List<Block> getBlocks(List<String> names) throws IOException {
        List<Block> result = new ArrayList<>();

        for (String name : names) {
            Block block = this.getBlock(name);

            if (block != null)
                result.add(block);
        }

        return result;
    }

    public List<BlockHeader> getBlockHeaders(List<String> names) throws IOException {
        List<BlockHeader> result = new ArrayList<>();

        for (String name : names) {
            Block block = this.getBlock(name);

            if (block != null) {
                result.add(block.getHeader());
                continue;
            }

            BlockHeader blockHeader = this.getBlockHeader(name);

            if (blockHeader != null)
                result.add(blockHeader);
        }

        return result;
    }

    public void setBlock(String name, Block block) {
        this.blocks.put(name, block);
    }

    public Account getAccount(String name) throws IOException {
        if (!this.accounts.containsKey(name))
            return null;

        return this.accountStore.getAccount(this.accounts.get(name));
    }

    public Account getAccount(Address address) throws IOException {
        return this.accountStore.getAccount(address);
    }

    public Address getAccountAddress(String name) {
        return this.accounts.get(name);
    }

    public void setAccount(String name, Account account) throws IOException {
        Address address = FactoryHelper.createRandomAddress();
        this.accounts.put(name, address);

        if (!account.isEmpty())
            this.accountStore.putAccount(address, account);
    }

    public void setCode(Hash codeHash, byte[] code) throws IOException {
        this.codeStore.putCode(codeHash, code);
    }

    public byte[] getCode(Hash codeHash) throws IOException {
        return this.codeStore.getCode(codeHash);
    }

    public BlockChain getBlockChain() throws IOException {
        if (this.blockChain == null)
            this.blockChain = FactoryHelper.createBlockChainWithGenesis(this.stores, this.accountStore);

        return this.blockChain;
    }

    public BlockProcessor getBlockProcessor() throws IOException {
        if (this.blockProcessor == null)
            this.blockProcessor = new BlockProcessor(this.getBlockChain(), new OrphanBlocks(), new SimpleBlockValidator(), new TransactionPool());

        return this.blockProcessor;
    }
}

