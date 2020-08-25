package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.TransactionReceipt;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Coin;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.execution.*;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.*;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.vms.eth.BlockData;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 03/06/2019.
 */
public class BlockValidatorTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void validEmptyBlock() throws IOException {
        Block genesis = GenesisGenerator.generateGenesis();
        Block block = FactoryHelper.createBlock(genesis, FactoryHelper.createRandomAddress(), 0);

        CodeStore codeStore = new CodeStore(new HashMapStore());
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider, null, codeStore);

        BlockValidator blockValidator = new BlockValidator(blockExecutor);

        Assert.assertTrue(blockValidator.isValid(genesis, null));
        Assert.assertTrue(blockValidator.isValid(block, genesis));
    }

    @Test
    public void validBlockWithTransaction() throws IOException {
        CodeStore codeStore = new CodeStore(new HashMapStore());
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);
        AccountStore accountStore = new AccountStore(trieStore.retrieve(Trie.EMPTY_TRIE_HASH));

        Account sender = new Account(Coin.fromUnsignedLong(10000), 0, 0, null, null);
        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        accountStore.putAccount(senderAddress, sender);
        accountStore.save();

        Transaction transaction = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(1000), 0, null, 6000000, Coin.ZERO);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        ExecutionContext executionContext = new TopExecutionContext(accountStore, null, codeStore);
        TransactionExecutor transactionExecutor = new TransactionExecutor(executionContext);

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        // TODO use difficulty instead of a constant
        BlockData blockData = new BlockData(genesis.getNumber() + 1, 0, FactoryHelper.createRandomAddress(), Difficulty.ONE, 0);

        // TODO evaluate to use BlockExecutor instead of TransactionExecutor
        List<TransactionReceipt> transactionReceipts = transactionExecutor.executeTransactions(transactions, blockData);

        Block block = new Block(genesis.getNumber() + 1, genesis.getHash(), null, transactions, BlockExecutionResult.calculateTransactionReceiptsHash(transactionReceipts), accountStore.getRootHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), null);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider, null, codeStore);

        BlockValidator blockValidator = new BlockValidator(blockExecutor);

        Assert.assertTrue(blockValidator.isValid(genesis, null));
        Assert.assertTrue(blockValidator.isValid(block, genesis));
    }

    @Test
    public void invalidBlockWithInvalidTransactionsRoot() {
        Transaction transaction = FactoryHelper.createTransaction(1000);
        Transaction transaction2 = FactoryHelper.createTransaction(2000);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        List<Transaction> transactions2 = new ArrayList<>();
        transactions2.add(transaction2);

        Block block0 = new Block(1, FactoryHelper.createRandomBlockHash(), null, transactions, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), null);
        Block block = new Block(block0.getHeader(), null, transactions2);

        BlockValidator blockValidator = new BlockValidator(null);

        Assert.assertTrue(blockValidator.isValid(block0));
        Assert.assertFalse(blockValidator.isValid(block));
    }

    @Test
    public void invalidBlockWithInvalidNumberOfTransactions() {
        Transaction transaction = FactoryHelper.createTransaction(1000);
        Transaction transaction2 = FactoryHelper.createTransaction(2000);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);
        List<Transaction> transactions2 = new ArrayList<>();
        transactions2.add(transaction);
        transactions2.add(transaction2);

        Block block0 = new Block(1, FactoryHelper.createRandomBlockHash(), null, transactions, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), null);
        Block block = new Block(block0.getHeader(), null, transactions2);

        BlockValidator blockValidator = new BlockValidator(null);

        Assert.assertTrue(blockValidator.isValid(block0));
        Assert.assertFalse(blockValidator.isValid(block));
    }

    @Test
    public void cannotBuildBlockWithInvalidTransaction() throws IOException {
        CodeStore codeStore = new CodeStore(new HashMapStore());
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);
        AccountStore accountStore = new AccountStore(trieStore.retrieve(Trie.EMPTY_TRIE_HASH));

        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Transaction transaction = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(1000), 0, null, 6000000, Coin.ZERO);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        ExecutionContext executionContext = new TopExecutionContext(accountStore, null, codeStore);
        TransactionExecutor transactionExecutor = new TransactionExecutor(executionContext);

        // TODO use difficulty instead of a constant
        BlockData blockData = new BlockData(genesis.getNumber() + 1, 0, FactoryHelper.createRandomAddress(), Difficulty.ONE, 0);

        // TODO evaluate to use BlockExecutor instead of TransactionExecutor
        List<TransactionReceipt> transactionReceipts = transactionExecutor.executeTransactions(transactions, blockData);

        exception.expect(NullPointerException.class);
        new Block(genesis.getNumber() + 1, genesis.getHash(), null, transactions, BlockExecutionResult.calculateTransactionReceiptsHash(transactionReceipts), genesis.getStateRootHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), Difficulty.ONE);
    }

    @Test
    public void invalidEmptyBlock() throws IOException {
        CodeStore codeStore = new CodeStore(new HashMapStore());
        Block genesis = GenesisGenerator.generateGenesis();
        Block block = new Block(genesis.getNumber() + 1, genesis.getHash(), null, new ArrayList<>(), null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), null);

        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider, null, codeStore);

        BlockValidator blockValidator = new BlockValidator(blockExecutor);

        Assert.assertTrue(blockValidator.isValid(genesis, null));
        Assert.assertFalse(blockValidator.isValid(block, genesis));
    }
}
