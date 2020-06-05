package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.*;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.*;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.vms.eth.OpCodes;
import com.ajlopez.blockchain.vms.eth.TrieStorage;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by ajlopez on 24/01/2018.
 */
public class MinerProcessorTest {
    @Test
    public void mineBlockWithNoTransactions()  throws IOException {
        TransactionPool transactionPool = new TransactionPool();
        Address coinbase = FactoryHelper.createRandomAddress();
        Stores stores = new MemoryStores();

        MinerProcessor processor = new MinerProcessor(null, transactionPool, stores, coinbase);

        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Block parent = new Block(1L, hash, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Block block = processor.mineBlock(parent);

        Assert.assertNotNull(block);
        Assert.assertEquals(2, block.getNumber());
        Assert.assertEquals(parent.getHash(), block.getParentHash());

        List<Transaction> txs = block.getTransactions();

        Assert.assertNotNull(txs);
        Assert.assertTrue(txs.isEmpty());
    }

    @Test
    public void mineBlockWithOneTransaction() throws IOException {
        Transaction tx = FactoryHelper.createTransaction(100);

        TransactionPool transactionPool = new TransactionPool();
        transactionPool.addTransaction(tx);

        Stores stores = new MemoryStores();
        AccountStore accountStore = stores.getAccountStoreProvider().retrieve(Trie.EMPTY_TRIE_HASH);

        FactoryHelper.createAccountWithBalance(accountStore, tx.getSender(), 1000);

        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block parent = new Block(1L, hash, null, accountStore.getRootHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        AccountStoreProvider accountStoreProvider = stores.getAccountStoreProvider();
        MinerProcessor processor = new MinerProcessor(null, transactionPool, stores, coinbase);

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
    public void mineBlockWithOneTransactionExecutingCode() throws IOException {
        byte[] code = new byte[] {
                OpCodes.PUSH1, 0x01, OpCodes.PUSH1, 0x00, OpCodes.SSTORE
        };

        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Stores stores = new MemoryStores();

        CodeStore codeStore = stores.getCodeStore();
        AccountStoreProvider accountStoreProvider = stores.getAccountStoreProvider();
        AccountStore accountStore = accountStoreProvider.retrieve(Trie.EMPTY_TRIE_HASH);

        FactoryHelper.createAccountWithBalance(accountStore, senderAddress, 1000000);
        FactoryHelper.createAccountWithCode(accountStore, codeStore, receiverAddress, code);

        Transaction tx = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(100), 0, new byte[] { 0x01, 0x02, 0x03, 0x04 }, 200000, Coin.ZERO);

        TransactionPool transactionPool = new TransactionPool();
        transactionPool.addTransaction(tx);

        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block parent = new Block(1L, hash, null, accountStore.getRootHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        MinerProcessor processor = new MinerProcessor(null, transactionPool, stores, coinbase);

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
        Assert.assertEquals(Coin.fromUnsignedLong(1000000 - 100), updatedSenderAccount.getBalance());

        Assert.assertNotNull(updatedReceiverAccount);
        Assert.assertEquals(0, updatedReceiverAccount.getNonce());
        Assert.assertEquals(Coin.fromUnsignedLong(100), updatedReceiverAccount.getBalance());
        Assert.assertNotEquals(Trie.EMPTY_TRIE_HASH, updatedReceiverAccount.getStorageHash());

        TrieStorage trieStorage = stores.getTrieStorageProvider().retrieve(updatedReceiverAccount.getStorageHash());

        Assert.assertNotNull(trieStorage);
        Assert.assertEquals(DataWord.ONE, trieStorage.getValue(DataWord.ZERO));
    }

    @Test
    public void mineBlockWithOneTransactionExecutingCodeAccesingBlockData() throws IOException {
        byte[] code = new byte[] {
                OpCodes.NUMBER, OpCodes.PUSH1, 0x00, OpCodes.SSTORE
        };

        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Stores stores = new MemoryStores();

        CodeStore codeStore = stores.getCodeStore();
        AccountStoreProvider accountStoreProvider = stores.getAccountStoreProvider();
        AccountStore accountStore = accountStoreProvider.retrieve(Trie.EMPTY_TRIE_HASH);

        FactoryHelper.createAccountWithBalance(accountStore, senderAddress, 1000000);
        FactoryHelper.createAccountWithCode(accountStore, codeStore, receiverAddress, code);

        Transaction tx = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(100), 0, new byte[] { 0x01, 0x02, 0x03, 0x04 }, 200000, Coin.ZERO);

        TransactionPool transactionPool = new TransactionPool();
        transactionPool.addTransaction(tx);

        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block parent = new Block(41L, hash, null, accountStore.getRootHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        MinerProcessor processor = new MinerProcessor(null, transactionPool, stores, coinbase);

        Block block = processor.mineBlock(parent);

        Assert.assertNotNull(block);
        Assert.assertEquals(42, block.getNumber());
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
        Assert.assertEquals(Coin.fromUnsignedLong(1000000 - 100), updatedSenderAccount.getBalance());

        Assert.assertNotNull(updatedReceiverAccount);
        Assert.assertEquals(0, updatedReceiverAccount.getNonce());
        Assert.assertEquals(Coin.fromUnsignedLong(100), updatedReceiverAccount.getBalance());
        Assert.assertNotEquals(Trie.EMPTY_TRIE_HASH, updatedReceiverAccount.getStorageHash());

        TrieStorage trieStorage = stores.getTrieStorageProvider().retrieve(updatedReceiverAccount.getStorageHash());

        Assert.assertNotNull(trieStorage);
        Assert.assertEquals(DataWord.fromUnsignedInteger(42), trieStorage.getValue(DataWord.ZERO));
    }

    @Test
    public void processBlockWithOneTransaction() throws IOException {
        Address sender = FactoryHelper.createRandomAddress();
        Transaction tx = FactoryHelper.createTransaction(100, sender, 0);

        TransactionPool transactionPool = new TransactionPool();
        transactionPool.addTransaction(tx);

        Stores stores = new MemoryStores();
        AccountStore accountStore = stores.getAccountStoreProvider().retrieve(Trie.EMPTY_TRIE_HASH);

        Account account = new Account(Coin.fromUnsignedLong(1000), 0, 0, null, null);
        accountStore.putAccount(sender, account);
        accountStore.save();

        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis(stores, accountStore);
        Address coinbase = FactoryHelper.createRandomAddress();

        MinerProcessor processor = new MinerProcessor(blockChain, transactionPool, stores, coinbase);

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
    public void mineOneBlockUsingStartAndStop() throws InterruptedException, IOException {
        Transaction tx = FactoryHelper.createTransaction(100);

        TransactionPool transactionPool = new TransactionPool();
        transactionPool.addTransaction(tx);

        Stores stores = new MemoryStores();
        AccountStore accountStore = stores.getAccountStoreProvider().retrieve(Trie.EMPTY_TRIE_HASH);

        Account account = new Account(Coin.fromUnsignedLong(1000), 0, 0, null, null);
        accountStore.putAccount(tx.getSender(), account);
        accountStore.save();

        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis(stores, accountStore);
        Address coinbase = FactoryHelper.createRandomAddress();

        MinerProcessor processor = new MinerProcessor(blockChain, transactionPool, stores, coinbase);

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
    public void mineTwoBlocksUsingStartAndStop() throws InterruptedException, IOException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();

        TransactionPool transactionPool = new TransactionPool();
        Address coinbase = FactoryHelper.createRandomAddress();

        MinerProcessor processor = new MinerProcessor(blockChain, transactionPool, new MemoryStores(), coinbase);

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
