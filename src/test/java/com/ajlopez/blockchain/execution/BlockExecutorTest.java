package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.bc.GenesisGenerator;
import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.types.*;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.*;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtils;
import com.ajlopez.blockchain.vms.eth.BlockData;
import com.ajlopez.blockchain.vms.eth.OpCodes;
import com.ajlopez.blockchain.vms.eth.Storage;
import com.ajlopez.blockchain.vms.eth.TrieStorageProvider;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 01/06/2019.
 */
public class BlockExecutorTest {
    @Test
    public void executeBlockWithoutTransactions() throws IOException {
        CodeStore codeStore = new CodeStore(new HashMapStore());
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider, null, codeStore);

        Block genesis = GenesisGenerator.generateGenesis();
        Block block = FactoryHelper.createBlock(genesis, FactoryHelper.createRandomAddress(), 0);

        Hash result = blockExecutor.executeBlock(block, genesis.getStateRootHash());

        Assert.assertEquals(genesis.getStateRootHash(), result);
        Assert.assertEquals(Trie.EMPTY_TRIE_HASH, result);
    }

    @Test
    public void executeBlockWithOneTransaction() throws IOException {
        CodeStore codeStore = new CodeStore(new HashMapStore());
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);
        AccountStore accountStore = new AccountStore(trieStore.retrieve(Trie.EMPTY_TRIE_HASH));

        Address senderAddress = FactoryHelper.createAccountWithBalance(accountStore, 10000);
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        Transaction transaction = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(1000), 0, null, 6000000, Coin.ZERO);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        ExecutionContext executionContext = new TopExecutionContext(accountStore, null, codeStore);
        TransactionExecutor transactionExecutor = new TransactionExecutor(executionContext);

        transactionExecutor.executeTransactions(transactions, null);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider, null, codeStore);

        Block block = new Block(genesis.getNumber() + 1, genesis.getHash(), null, transactions, accountStore.getRootHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), null);

        Hash result = blockExecutor.executeBlock(block, genesis.getStateRootHash());

        Assert.assertEquals(accountStore.getRootHash(), result);
    }

    @Test
    public void executeBlockWithOneTransactionGettingBlockData() throws IOException {
        byte[] code = new byte[] {
                OpCodes.NUMBER, OpCodes.PUSH1, 0x00, OpCodes.SSTORE,
                OpCodes.TIMESTAMP, OpCodes.PUSH1, 0x01, OpCodes.SSTORE,
                OpCodes.COINBASE, OpCodes.PUSH1, 0x02, OpCodes.SSTORE,
                OpCodes.DIFFICULTY, OpCodes.PUSH1, 0x03, OpCodes.SSTORE
        };

        Hash codeHash = HashUtils.calculateHash(code);

        CodeStore codeStore = new CodeStore(new HashMapStore());
        codeStore.putCode(codeHash, code);

        TrieStore trieStore = new TrieStore(new HashMapStore());
        TrieStorageProvider trieStorageProvider = new TrieStorageProvider(trieStore);
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);
        AccountStore accountStore = new AccountStore(trieStore.retrieve(Trie.EMPTY_TRIE_HASH));

        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        FactoryHelper.createAccountWithBalance(accountStore, senderAddress, 10000);
        FactoryHelper.createAccountWithCode(accountStore, codeStore, receiverAddress, code);

        accountStore.save();

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        Transaction transaction = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(1000), 0, null, 6000000, Coin.ZERO);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        ExecutionContext executionContext = new TopExecutionContext(accountStore, trieStorageProvider, codeStore);
        TransactionExecutor transactionExecutor = new TransactionExecutor(executionContext);

        Block block = new Block(genesis.getNumber() + 1, genesis.getHash(), null, transactions, accountStore.getRootHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), Difficulty.TWO);

        BlockData blockData = new BlockData(block.getNumber(), block.getTimestamp(), block.getCoinbase(), block.getDifficulty());
        transactionExecutor.executeTransactions(transactions, blockData);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider, trieStorageProvider, codeStore);

        Hash result = blockExecutor.executeBlock(block, genesis.getStateRootHash());

        Assert.assertEquals(accountStore.getRootHash(), result);

        Account receiver = accountStore.getAccount(receiverAddress);
        Assert.assertNotNull(receiver);
        Assert.assertNotNull(receiver.getStorageHash());

        Storage storage = trieStorageProvider.retrieve(receiver.getStorageHash());

        Assert.assertNotNull(storage);
        Assert.assertEquals(DataWord.fromUnsignedLong(block.getNumber()), storage.getValue(DataWord.ZERO));
        Assert.assertEquals(DataWord.fromUnsignedLong(block.getTimestamp()), storage.getValue(DataWord.ONE));
        Assert.assertEquals(DataWord.fromAddress(block.getCoinbase()), storage.getValue(DataWord.TWO));
        Assert.assertEquals(DataWord.TWO, storage.getValue(DataWord.fromUnsignedInteger(3)));
    }

    @Test
    public void executeBlockWithOneTransactionWithInvalidNonce() throws IOException {
        CodeStore codeStore = new CodeStore(new HashMapStore());
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);
        AccountStore accountStore = new AccountStore(trieStore.retrieve(Trie.EMPTY_TRIE_HASH));

        Address senderAddress = FactoryHelper.createAccountWithBalance(accountStore,10000);
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        Transaction transaction = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(1000), 1, null, 6000000, Coin.ZERO);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        ExecutionContext executionContext = new TopExecutionContext(accountStore, null, codeStore);
        TransactionExecutor transactionExecutor = new TransactionExecutor(executionContext);

        transactionExecutor.executeTransactions(transactions, null);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider, null, codeStore);

        Block block = new Block(genesis.getNumber() + 1, genesis.getHash(), null, transactions, accountStore.getRootHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), Difficulty.TEN);

        Hash result = blockExecutor.executeBlock(block, genesis.getStateRootHash());

        Assert.assertEquals(genesis.getStateRootHash(), result);
    }

    @Test
    public void executeBlockWithOneTransactionWithSenderWithoutEnoughBalance() throws IOException {
        CodeStore codeStore = new CodeStore(new HashMapStore());
        TrieStore trieStore = new TrieStore(new HashMapStore());
        AccountStoreProvider accountStoreProvider = new AccountStoreProvider(trieStore);
        AccountStore accountStore = new AccountStore(trieStore.retrieve(Trie.EMPTY_TRIE_HASH));

        Address senderAddress = FactoryHelper.createAccountWithBalance(accountStore, 10000);
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        Transaction transaction = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(10000000), 0, null, 6000000, Coin.ZERO);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        ExecutionContext executionContext = new TopExecutionContext(accountStore, null, codeStore);
        TransactionExecutor transactionExecutor = new TransactionExecutor(executionContext);

        transactionExecutor.executeTransactions(transactions, null);

        BlockExecutor blockExecutor = new BlockExecutor(accountStoreProvider, null, codeStore);

        Block block = new Block(genesis.getNumber() + 1, genesis.getHash(), null, transactions, accountStore.getRootHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), null);

        Hash result = blockExecutor.executeBlock(block, genesis.getStateRootHash());

        Assert.assertEquals(genesis.getStateRootHash(), result);
    }
}
