package com.ajlopez.blockchain.execution;

import com.ajlopez.blockchain.bc.GenesisGenerator;
import com.ajlopez.blockchain.core.Account;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.Transaction;
import com.ajlopez.blockchain.core.TransactionReceipt;
import com.ajlopez.blockchain.core.types.*;
import com.ajlopez.blockchain.merkle.MerkleTree;
import com.ajlopez.blockchain.merkle.MerkleTreeBuilder;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.*;
import com.ajlopez.blockchain.test.builders.ExecutorBuilder;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtils;
import com.ajlopez.blockchain.vms.eth.BlockData;
import com.ajlopez.blockchain.vms.eth.OpCodes;
import com.ajlopez.blockchain.vms.eth.Storage;
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
        ExecutorBuilder builder = new ExecutorBuilder();

        BlockExecutor blockExecutor = builder.buildBlockExecutor();

        Block genesis = GenesisGenerator.generateGenesis();
        Block block = FactoryHelper.createBlock(genesis, FactoryHelper.createRandomAddress(), 0);

        BlockExecutionResult result = blockExecutor.executeBlock(block, genesis.getStateRootHash());

        Assert.assertEquals(genesis.getStateRootHash(), result.getStateRootHash());
        Assert.assertEquals(Trie.EMPTY_TRIE_HASH, result.getStateRootHash());
        Assert.assertTrue(result.getTransactionReceipts().isEmpty());

        Assert.assertEquals(MerkleTree.EMPTY_MERKLE_TREE_HASH, result.getTransactionReceiptsHash());
    }

    @Test
    public void executeBlockWithOneTransaction() throws IOException {
        ExecutorBuilder builder = new ExecutorBuilder();
        BlockExecutor blockExecutor = builder.buildBlockExecutor();
        AccountStore accountStore = builder.getAccountStore();

        Address senderAddress = FactoryHelper.createAccountWithBalance(accountStore, 10000);
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        Transaction transaction = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(1000), 0, null, 6000000, Coin.ZERO);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        TransactionExecutor transactionExecutor = builder.buildTransactionExecutor();

        transactionExecutor.executeTransactions(transactions, null);

        Block block = new Block(genesis.getNumber() + 1, genesis.getHash(), null, transactions, null, accountStore.getRootHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), null, 0);

        BlockExecutionResult result = blockExecutor.executeBlock(block, genesis.getStateRootHash());

        Assert.assertEquals(accountStore.getRootHash(), result.getStateRootHash());
        Assert.assertFalse(result.getTransactionReceipts().isEmpty());
        Assert.assertEquals(1, result.getTransactionReceipts().size());

        MerkleTreeBuilder merkleTreeBuilder = new MerkleTreeBuilder();

        for (TransactionReceipt transactionReceipt : result.getTransactionReceipts())
            merkleTreeBuilder.add(transactionReceipt.getHash());

        MerkleTree merkleTree = merkleTreeBuilder.build();

        Assert.assertEquals(merkleTree.getHash(), result.getTransactionReceiptsHash());
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

        ExecutorBuilder builder = new ExecutorBuilder();

        CodeStore codeStore = builder.getCodeStore();
        codeStore.putCode(codeHash, code);

        AccountStore accountStore = builder.getAccountStore();

        Address senderAddress = FactoryHelper.createRandomAddress();
        Address receiverAddress = FactoryHelper.createRandomAddress();

        FactoryHelper.createAccountWithBalance(accountStore, senderAddress, 10000);
        FactoryHelper.createAccountWithCode(accountStore, codeStore, receiverAddress, code);

        accountStore.save();

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        Transaction transaction = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(1000), 0, null, 6000000, Coin.ZERO);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        TransactionExecutor transactionExecutor = builder.buildTransactionExecutor();

        Block block = new Block(genesis.getNumber() + 1, genesis.getHash(), null, transactions, null, accountStore.getRootHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), Difficulty.TWO, 0);

        BlockData blockData = new BlockData(block.getNumber(), block.getTimestamp(), block.getCoinbase(), block.getDifficulty(), 0);
        transactionExecutor.executeTransactions(transactions, blockData);

        BlockExecutor blockExecutor = builder.buildBlockExecutor();

        BlockExecutionResult result = blockExecutor.executeBlock(block, genesis.getStateRootHash());

        Assert.assertEquals(accountStore.getRootHash(), result.getStateRootHash());

        Account receiver = accountStore.getAccount(receiverAddress);
        Assert.assertNotNull(receiver);
        Assert.assertNotNull(receiver.getStorageHash());

        Storage storage = builder.getTrieStorageProvider().retrieve(receiver.getStorageHash());

        Assert.assertNotNull(storage);
        Assert.assertEquals(DataWord.fromUnsignedLong(block.getNumber()), storage.getValue(DataWord.ZERO));
        Assert.assertEquals(DataWord.fromUnsignedLong(block.getTimestamp()), storage.getValue(DataWord.ONE));
        Assert.assertEquals(DataWord.fromAddress(block.getCoinbase()), storage.getValue(DataWord.TWO));
        Assert.assertEquals(DataWord.TWO, storage.getValue(DataWord.fromUnsignedInteger(3)));
    }

    @Test
    public void executeBlockWithOneTransactionWithInvalidNonce() throws IOException {
        ExecutorBuilder builder = new ExecutorBuilder();
        AccountStore accountStore = builder.getAccountStore();

        Address senderAddress = FactoryHelper.createAccountWithBalance(accountStore,10000);
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        Transaction transaction = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(1000), 1, null, 6000000, Coin.ZERO);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        TransactionExecutor transactionExecutor = builder.buildTransactionExecutor();

        transactionExecutor.executeTransactions(transactions, null);

        BlockExecutor blockExecutor = builder.buildBlockExecutor();

        Block block = new Block(genesis.getNumber() + 1, genesis.getHash(), null, transactions, null, accountStore.getRootHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), Difficulty.TEN, 0);

        BlockExecutionResult result = blockExecutor.executeBlock(block, genesis.getStateRootHash());

        Assert.assertEquals(genesis.getStateRootHash(), result.getStateRootHash());
    }

    @Test
    public void executeBlockWithOneTransactionWithSenderWithoutEnoughBalance() throws IOException {
        ExecutorBuilder builder = new ExecutorBuilder();
        AccountStore accountStore = builder.getAccountStore();

        Address senderAddress = FactoryHelper.createAccountWithBalance(accountStore, 10000);
        Address receiverAddress = FactoryHelper.createRandomAddress();

        Block genesis = GenesisGenerator.generateGenesis(accountStore);

        Transaction transaction = new Transaction(senderAddress, receiverAddress, Coin.fromUnsignedLong(10000000), 0, null, 6000000, Coin.ZERO);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        TransactionExecutor transactionExecutor = builder.buildTransactionExecutor();

        transactionExecutor.executeTransactions(transactions, null);

        BlockExecutor blockExecutor = builder.buildBlockExecutor();

        Block block = new Block(genesis.getNumber() + 1, genesis.getHash(), null, transactions, null, accountStore.getRootHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), null, 0);

        BlockExecutionResult result = blockExecutor.executeBlock(block, genesis.getStateRootHash());

        Assert.assertEquals(genesis.getStateRootHash(), result.getStateRootHash());
    }
}
