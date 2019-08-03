package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.bc.GenesisGenerator;
import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.*;
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
        CodeStore codeStore = new CodeStore(new HashMapStore());
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider, codeStore);

        Block genesis = GenesisGenerator.generateGenesis();
        Block block = FactoryHelper.createBlock(genesis, FactoryHelper.createRandomAddress(), 0);

        Hash result = blockExecutor.executeBlock(block, genesis.getStateRootHash());

        Assert.assertEquals(genesis.getStateRootHash(), result);
        Assert.assertEquals(Trie.EMPTY_TRIE_HASH, result);
    }

    @Test
    public void executeBlockWithOneTransaction() {
        CodeStore codeStore = new CodeStore(new HashMapStore());
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);
        AccountStore accountStore = new AccountStore(trieStore.retrieve(Trie.EMPTY_TRIE_HASH));

        Account sender = new Account(BigInteger.valueOf(10000), 0, null, null);
        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        accountStore.putAccount(senderAddress, sender);
        accountStore.save();

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        Transaction transaction = new Transaction(senderAddress, receiverAddress, BigInteger.valueOf(1000), 0, null, 6000000, BigInteger.ZERO);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        ExecutionContext executionContext = new TopExecutionContext(accountStore, null, codeStore);
        TransactionExecutor transactionExecutor = new TransactionExecutor(executionContext);

        transactionExecutor.executeTransactions(transactions);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider, codeStore);

        Block block = new Block(genesis.getNumber() + 1, genesis.getHash(), transactions, accountStore.getRootHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress());

        Hash result = blockExecutor.executeBlock(block, genesis.getStateRootHash());

        Assert.assertEquals(accountStore.getRootHash(), result);
    }

    @Test
    public void executeBlockWithOneTransactionWithInvalidNonce() {
        CodeStore codeStore = new CodeStore(new HashMapStore());
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);
        AccountStore accountStore = new AccountStore(trieStore.retrieve(Trie.EMPTY_TRIE_HASH));

        Account sender = new Account(BigInteger.valueOf(10000), 0, null, null);
        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        accountStore.putAccount(senderAddress, sender);
        accountStore.save();

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        Transaction transaction = new Transaction(senderAddress, receiverAddress, BigInteger.valueOf(1000), 1, null, 6000000, BigInteger.ZERO);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        ExecutionContext executionContext = new TopExecutionContext(accountStore, null, codeStore);
        TransactionExecutor transactionExecutor = new TransactionExecutor(executionContext);

        transactionExecutor.executeTransactions(transactions);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider, codeStore);

        Block block = new Block(genesis.getNumber() + 1, genesis.getHash(), transactions, accountStore.getRootHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress());

        Hash result = blockExecutor.executeBlock(block, genesis.getStateRootHash());

        Assert.assertEquals(genesis.getStateRootHash(), result);
    }

    @Test
    public void executeBlockWithOneTransactionWithSenderWithoutEnoughBalance() {
        CodeStore codeStore = new CodeStore(new HashMapStore());
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);
        AccountStore accountStore = new AccountStore(trieStore.retrieve(Trie.EMPTY_TRIE_HASH));

        Account sender = new Account(BigInteger.valueOf(10000), 0, null, null);
        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        accountStore.putAccount(senderAddress, sender);
        accountStore.save();

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        Transaction transaction = new Transaction(senderAddress, receiverAddress, BigInteger.valueOf(10000000), 0, null, 6000000, BigInteger.ZERO);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        ExecutionContext executionContext = new TopExecutionContext(accountStore, null, codeStore);
        TransactionExecutor transactionExecutor = new TransactionExecutor(executionContext);

        transactionExecutor.executeTransactions(transactions);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider, codeStore);

        Block block = new Block(genesis.getNumber() + 1, genesis.getHash(), transactions, accountStore.getRootHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress());

        Hash result = blockExecutor.executeBlock(block, genesis.getStateRootHash());

        Assert.assertEquals(genesis.getStateRootHash(), result);
    }
}
