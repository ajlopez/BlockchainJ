package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.AccountStoreProvider;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.TrieStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by ajlopez on 24/01/2018.
 */
public class MinerProcessorTest {
    @Test
    public void mineBlockWithNoTransactions() {
        TransactionPool transactionPool = new TransactionPool();
        Address coinbase = FactoryHelper.createRandomAddress();

        MinerProcessor processor = new MinerProcessor(null, transactionPool, new AccountStoreProvider(new TrieStore(new HashMapStore())), coinbase);

        BlockHash hash = new BlockHash(FactoryHelper.createRandomHash());
        Block parent = new Block(1L, hash, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase);

        Block block = processor.mineBlock(parent);

        Assert.assertNotNull(block);
        Assert.assertEquals(2, block.getNumber());
        Assert.assertEquals(parent.getHash(), block.getParentHash());

        List<Transaction> txs = block.getTransactions();

        Assert.assertNotNull(txs);
        Assert.assertTrue(txs.isEmpty());
    }

    @Test
    public void mineBlockWithOneTransaction() {
        Transaction tx = FactoryHelper.createTransaction(100);

        TransactionPool transactionPool = new TransactionPool();
        transactionPool.addTransaction(tx);

        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStore accountStore = new AccountStore(trieStore.retrieve(Trie.EMPTY_TRIE_HASH));

        Account account = new Account(Coin.fromUnsignedLong(1000), 0, null, null);
        accountStore.putAccount(tx.getSender(), account);
        accountStore.save();

        BlockHash hash = new BlockHash(FactoryHelper.createRandomHash());
        Address coinbase = FactoryHelper.createRandomAddress();

        Block parent = new Block(1L, hash, accountStore.getRootHash(), System.currentTimeMillis() / 1000, coinbase);

        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);
        MinerProcessor processor = new MinerProcessor(null, transactionPool, accountStoreProvider, coinbase);

        Block block = processor.mineBlock(parent);

        Assert.assertNotNull(block);
        Assert.assertEquals(2, block.getNumber());
        Assert.assertEquals(parent.getHash(), block.getParentHash());
        Assert.assertEquals(coinbase, block.getCoinbase());

        List<Transaction> txs = block.getTransactions();

        Assert.assertNotNull(txs);
        Assert.assertFalse(txs.isEmpty());
        Assert.assertEquals(1, txs.size());
        Assert.assertSame(tx, txs.get(0));

        Assert.assertFalse(transactionPool.getTransactions().isEmpty());

        AccountStore newAccountStore = accountStoreProvider.retrieve(block.getStateRootHash());
        Account updatedSenderAccount = newAccountStore.getAccount(tx.getSender());
        Account updatedReceiverAccount = newAccountStore.getAccount(tx.getReceiver());

        Assert.assertNotNull(updatedSenderAccount);
        Assert.assertEquals(1, updatedSenderAccount.getNonce());
        Assert.assertEquals(Coin.fromUnsignedLong(900), updatedSenderAccount.getBalance());

        Assert.assertNotNull(updatedReceiverAccount);
        Assert.assertEquals(0, updatedReceiverAccount.getNonce());
        Assert.assertEquals(Coin.fromUnsignedLong(100), updatedReceiverAccount.getBalance());
    }

    @Test
    public void processBlockWithOneTransaction() {
        Address sender = FactoryHelper.createRandomAddress();
        Transaction tx = FactoryHelper.createTransaction(100, sender, 0);

        TransactionPool transactionPool = new TransactionPool();
        transactionPool.addTransaction(tx);

        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStore accountStore = new AccountStore(trieStore.retrieve(Trie.EMPTY_TRIE_HASH));

        Account account = new Account(Coin.fromUnsignedLong(1000), 0, null, null);
        accountStore.putAccount(sender, account);
        accountStore.save();

        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis(accountStore);
        Address coinbase = FactoryHelper.createRandomAddress();

        MinerProcessor processor = new MinerProcessor(blockChain, transactionPool, new AccountStoreProvider(trieStore), coinbase);

        Block block = processor.process();

        Assert.assertNotNull(block);
        Assert.assertEquals(1, block.getNumber());
        Assert.assertEquals(blockChain.getBlockByNumber(0).getHash(), block.getParentHash());

        List<Transaction> txs = block.getTransactions();

        Assert.assertNotNull(txs);
        Assert.assertFalse(txs.isEmpty());
        Assert.assertEquals(1, txs.size());
        Assert.assertSame(tx, txs.get(0));

        Assert.assertFalse(transactionPool.getTransactions().isEmpty());
    }

    @Test
    public void mineOneBlockUsingStartAndStop() throws InterruptedException {
        Transaction tx = FactoryHelper.createTransaction(100);

        TransactionPool transactionPool = new TransactionPool();
        transactionPool.addTransaction(tx);

        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStore accountStore = new AccountStore(trieStore.retrieve(Trie.EMPTY_TRIE_HASH));

        Account account = new Account(Coin.fromUnsignedLong(1000), 0, null, null);
        accountStore.putAccount(tx.getSender(), account);
        accountStore.save();

        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis(accountStore);
        Address coinbase = FactoryHelper.createRandomAddress();

        MinerProcessor processor = new MinerProcessor(blockChain, transactionPool, new AccountStoreProvider(trieStore), coinbase);

        Semaphore sem = new Semaphore(0, true);

        List<Block> minedBlocks = new ArrayList<>();

        processor.onMinedBlock((blk) -> {
            minedBlocks.add(blk);
            sem.release();
        });

        processor.start();

        sem.acquire();

        processor.stop();

        Assert.assertFalse(minedBlocks.isEmpty());
        Assert.assertEquals(1, minedBlocks.size());

        Block block = minedBlocks.get(0);

        Assert.assertNotNull(block);
        Assert.assertEquals(1, block.getNumber());
        Assert.assertEquals(blockChain.getBlockByNumber(0).getHash(), block.getParentHash());

        List<Transaction> txs = block.getTransactions();

        Assert.assertNotNull(txs);
        Assert.assertFalse(txs.isEmpty());
        Assert.assertEquals(1, txs.size());
        Assert.assertSame(tx, txs.get(0));

        Assert.assertFalse(transactionPool.getTransactions().isEmpty());
    }

    @Test
    public void mineTwoBlocksUsingStartAndStop() throws InterruptedException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();

        TransactionPool transactionPool = new TransactionPool();
        Address coinbase = FactoryHelper.createRandomAddress();

        MinerProcessor processor = new MinerProcessor(blockChain, transactionPool, new AccountStoreProvider(new TrieStore(new HashMapStore())), coinbase);

        Semaphore sem = new Semaphore(0, true);

        List<Block> minedBlocks = new ArrayList<>();

        processor.onMinedBlock((block) -> {
            minedBlocks.add(block);
            sem.release();
        });

        processor.start();

        sem.acquire();
        sem.acquire();

        processor.stop();

        Assert.assertFalse(minedBlocks.isEmpty());
        Assert.assertEquals(2, minedBlocks.size());

        Block block1 = minedBlocks.get(0);

        Assert.assertNotNull(block1);
        Assert.assertEquals(1, block1.getNumber());

        Block block2 = minedBlocks.get(1);

        Assert.assertNotNull(block2);
        Assert.assertEquals(1, block1.getNumber());

        Assert.assertTrue(transactionPool.getTransactions().isEmpty());
    }
}
