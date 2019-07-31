package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.execution.BlockExecutor;
import com.ajlopez.blockchain.execution.ExecutionContext;
import com.ajlopez.blockchain.execution.TopExecutionContext;
import com.ajlopez.blockchain.execution.TransactionExecutor;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.*;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 03/06/2019.
 */
public class BlockValidatorTest {
    @Test
    public void validEmptyBlock() {
        Block genesis = GenesisGenerator.generateGenesis();
        Block block = FactoryHelper.createBlock(genesis, FactoryHelper.createRandomAddress(), 0);

        CodeStore codeStore = new CodeStore(new HashMapStore());
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider, codeStore);

        BlockValidator blockValidator = new BlockValidator(blockExecutor);

        Assert.assertTrue(blockValidator.isValid(block, genesis.getStateRootHash()));
    }

    @Test
    public void validBlockWithTransaction() {
        CodeStore codeStore = new CodeStore(new HashMapStore());
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);
        AccountStore accountStore = new AccountStore(trieStore.retrieve(Trie.EMPTY_TRIE_HASH));

        Account sender = new Account(BigInteger.valueOf(10000), 0, null, null);
        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        accountStore.putAccount(senderAddress, sender);
        accountStore.save();

        Transaction transaction = new Transaction(senderAddress, receiverAddress, BigInteger.valueOf(1000), 0, null);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        ExecutionContext executionContext = new TopExecutionContext(accountStore, null, codeStore);
        TransactionExecutor transactionExecutor = new TransactionExecutor(executionContext);

        transactionExecutor.executeTransactions(transactions);

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        Block block = new Block(genesis.getNumber() + 1, genesis.getHash(), transactions, accountStore.getRootHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress());

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider, codeStore);

        BlockValidator blockValidator = new BlockValidator(blockExecutor);

        Assert.assertTrue(blockValidator.isValid(block, genesis.getStateRootHash()));
    }

    @Test
    public void validBlockWithInvaidTransaction() {
        CodeStore codeStore = new CodeStore(new HashMapStore());
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);
        AccountStore accountStore = new AccountStore(trieStore.retrieve(Trie.EMPTY_TRIE_HASH));

        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Transaction transaction = new Transaction(senderAddress, receiverAddress, BigInteger.valueOf(1000), 0, null);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        Block block = new Block(genesis.getNumber() + 1, genesis.getHash(), transactions, genesis.getStateRootHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress());

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider, codeStore);

        BlockValidator blockValidator = new BlockValidator(blockExecutor);

        Assert.assertTrue(blockValidator.isValid(block, genesis.getStateRootHash()));
    }

    @Test
    public void invalidEmptyBlock() {
        CodeStore codeStore = new CodeStore(new HashMapStore());
        Block genesis = GenesisGenerator.generateGenesis();
        Block block = new Block(genesis.getNumber() + 1, genesis.getHash(), new ArrayList<>(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress());

        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider, codeStore);

        BlockValidator blockValidator = new BlockValidator(blockExecutor);

        Assert.assertFalse(blockValidator.isValid(block, genesis.getStateRootHash()));
    }
}
