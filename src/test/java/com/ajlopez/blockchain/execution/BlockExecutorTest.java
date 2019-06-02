package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.bc.GenesisGenerator;
import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.AccountStore;
import com.ajlopez.blockchain.store.AccountStoreProvider;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.TrieStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 01/06/2019.
 */
public class BlockExecutorTest {
    @Test
    public void executeBlockWithoutTransactions() {
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider);

        Block genesis = GenesisGenerator.generateGenesis();
        Block block = FactoryHelper.createBlock(genesis, FactoryHelper.createRandomAddress(), 0);

        Hash result = blockExecutor.executeBlock(block, genesis.getStateRootHash());

        Assert.assertEquals(genesis.getStateRootHash(), result);
        Assert.assertEquals(Trie.EMPTY_TRIE_HASH, result);
    }

    @Test
    public void executeBlockWithOneTransaction() {
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);
        AccountStore accountStore = new AccountStore(trieStore.retrieve(Trie.EMPTY_TRIE_HASH));

        Account sender = new Account(BigInteger.valueOf(10000), 0, null, null);
        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        accountStore.putAccount(senderAddress, sender);
        accountStore.save();

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        Transaction transaction = new Transaction(senderAddress, receiverAddress, BigInteger.valueOf(1000), 0);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        ExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);
        TransactionExecutor transactionExecutor = new TransactionExecutor(executionContext);

        transactionExecutor.executeTransactions(transactions);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider);

        Block block = new Block(genesis.getNumber() + 1, genesis.getHash(), transactions, accountStore.getRootHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress());

        Hash result = blockExecutor.executeBlock(block, genesis.getStateRootHash());

        Assert.assertEquals(accountStore.getRootHash(), result);
    }

    @Test
    public void executeBlockWithOneTransactionWithInvalidNonce() {
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);
        AccountStore accountStore = new AccountStore(trieStore.retrieve(Trie.EMPTY_TRIE_HASH));

        Account sender = new Account(BigInteger.valueOf(10000), 0, null, null);
        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        accountStore.putAccount(senderAddress, sender);
        accountStore.save();

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        Transaction transaction = new Transaction(senderAddress, receiverAddress, BigInteger.valueOf(1000), 1);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        ExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);
        TransactionExecutor transactionExecutor = new TransactionExecutor(executionContext);

        transactionExecutor.executeTransactions(transactions);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider);

        Block block = new Block(genesis.getNumber() + 1, genesis.getHash(), transactions, accountStore.getRootHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress());

        Hash result = blockExecutor.executeBlock(block, genesis.getStateRootHash());

        Assert.assertEquals(genesis.getStateRootHash(), result);
    }

    @Test
    public void executeBlockWithOneTransactionWithSenderWithoutEnoughBalance() {
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);
        AccountStore accountStore = new AccountStore(trieStore.retrieve(Trie.EMPTY_TRIE_HASH));

        Account sender = new Account(BigInteger.valueOf(10000), 0, null, null);
        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        accountStore.putAccount(senderAddress, sender);
        accountStore.save();

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        Transaction transaction = new Transaction(senderAddress, receiverAddress, BigInteger.valueOf(10000000), 0);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        ExecutionContext executionContext = new TopExecutionContext(accountStore, null, null);
        TransactionExecutor transactionExecutor = new TransactionExecutor(executionContext);

        transactionExecutor.executeTransactions(transactions);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider);

        Block block = new Block(genesis.getNumber() + 1, genesis.getHash(), transactions, accountStore.getRootHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress());

        Hash result = blockExecutor.executeBlock(block, genesis.getStateRootHash());

        Assert.assertEquals(genesis.getStateRootHash(), result);
    }
}
