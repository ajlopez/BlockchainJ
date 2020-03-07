package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class BlockTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void createWithNumberAndParentHash() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1L, hash, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Assert.assertEquals(1L, block.getNumber());
        Assert.assertEquals(hash, block.getParentHash());
        Assert.assertNotNull(block.getHash());

        Assert.assertNotNull(block.getUncles());
        Assert.assertTrue(block.getUncles().isEmpty());

        Assert.assertEquals(block.getDifficulty(), block.getTotalDifficulty());

        Assert.assertNotNull(block.getTransactions());
        Assert.assertTrue(block.getTransactions().isEmpty());
    }

    @Test
    public void noTransactions() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1L, hash, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        List<Transaction> transactions = block.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertTrue(transactions.isEmpty());

        Assert.assertEquals(Trie.EMPTY_TRIE_HASH, block.getTransactionRootHash());
    }

    @Test
    public void cannotAddTransactionToEmptyTransactionList() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1L, hash, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        List<Transaction> transactions = block.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertTrue(transactions.isEmpty());

        exception.expect(UnsupportedOperationException.class);
        transactions.add(FactoryHelper.createTransaction(42));
    }

    @Test
    public void addingTransactionsToOriginalListDoesNotChangeBlockTransactionList() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();
        Transaction transaction = FactoryHelper.createTransaction(1000);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Block block = new Block(1L, hash, null, transactions, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Assert.assertNotNull(block.getTransactions());
        Assert.assertFalse(block.getTransactions().isEmpty());
        Assert.assertTrue(block.getTransactions().contains(transaction));
        Assert.assertEquals(1, block.getTransactions().size());

        transactions.add(FactoryHelper.createTransaction(2000));

        Assert.assertNotNull(block.getTransactions());
        Assert.assertFalse(block.getTransactions().isEmpty());
        Assert.assertTrue(block.getTransactions().contains(transaction));
        Assert.assertEquals(1, block.getTransactions().size());
    }

    @Test
    public void cannotAddUnclesToEmptyUncleList() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1L, hash, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        List<BlockHeader> uncles = block.getUncles();

        Assert.assertNotNull(uncles);
        Assert.assertTrue(uncles.isEmpty());

        exception.expect(UnsupportedOperationException.class);
        uncles.add(null);
    }

    @Test
    public void twoUncles() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();
        BlockHeader uncle1 = FactoryHelper.createBlockHeader(1);
        BlockHeader uncle2 = FactoryHelper.createBlockHeader(1);

        List<BlockHeader> uncles = new ArrayList<>();
        uncles.add(uncle1);
        uncles.add(uncle2);

        Block block = new Block(2L, hash, uncles, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        List<BlockHeader> result = block.getUncles();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(uncle1, result.get(0));
        Assert.assertEquals(uncle2, result.get(1));

        exception.expect(UnsupportedOperationException.class);
        result.add(null);

        long totdiff = block.getDifficulty().asBigInteger().longValue()
            + uncle1.getDifficulty().asBigInteger().longValue()
            + uncle2.getDifficulty().asBigInteger().longValue();

        Assert.assertEquals(Difficulty.fromUnsignedLong(totdiff), block.getTotalDifficulty());
    }

    @Test
    public void addUncleToOriginalUncleList() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();
        BlockHeader uncle1 = FactoryHelper.createBlockHeader(1);
        BlockHeader uncle2 = FactoryHelper.createBlockHeader(1);
        BlockHeader uncle3 = FactoryHelper.createBlockHeader(1);

        List<BlockHeader> uncles = new ArrayList<>();
        uncles.add(uncle1);
        uncles.add(uncle2);

        Block block = new Block(2L, hash, uncles, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        List<BlockHeader> result = block.getUncles();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(uncle1, result.get(0));
        Assert.assertEquals(uncle2, result.get(1));

        uncles.add(uncle3);

        List<BlockHeader> result2 = block.getUncles();

        Assert.assertNotNull(result2);
        Assert.assertFalse(result2.isEmpty());
        Assert.assertEquals(2, result2.size());
        Assert.assertEquals(uncle1, result.get(0));
        Assert.assertEquals(uncle2, result2.get(1));
    }

    @Test
    public void blockWithDifferentParentHashesHaveDifferentHashes() {
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block1 = new Block(1L, FactoryHelper.createRandomBlockHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Block block2 = new Block(1L, FactoryHelper.createRandomBlockHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Assert.assertNotEquals(block1.getHash(), block2.getHash());
    }

    @Test
    public void blockWithDifferentNumbersHaveDifferentHashes() {
        BlockHash parentHash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block1 = new Block(1L, parentHash, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Block block2 = new Block(2L, parentHash, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Assert.assertNotEquals(block1.getHash(), block2.getHash());
    }

    @Test
    public void blockWithDifferentTransactionsHaveDifferentHashes() {
        Transaction tx1 = FactoryHelper.createTransaction(42);
        Transaction tx2 = FactoryHelper.createTransaction(144);

        List<Transaction> txs1 = new ArrayList<>();
        List<Transaction> txs2 = new ArrayList<>();

        txs1.add(tx1);
        txs2.add(tx2);

        BlockHash hash = FactoryHelper.createRandomBlockHash();

        Address coinbase = FactoryHelper.createRandomAddress();

        Block block1 = new Block(1L, hash, null, txs1, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);
        Block block2 = new Block(1L, hash, null, txs2, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        Assert.assertNotEquals(block1.getHash(), block2.getHash());

        Assert.assertEquals(Block.calculateTransactionsRootHash(txs1), block1.getTransactionRootHash());
        Assert.assertEquals(Block.calculateTransactionsRootHash(txs2), block2.getTransactionRootHash());
    }

    @Test
    public void withOneTransaction() {
        Transaction tx = FactoryHelper.createTransaction(42);

        List<Transaction> txs = new ArrayList<>();
        txs.add(tx);

        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1L, hash, null, txs, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        List<Transaction> transactions = block.getTransactions();

        Assert.assertNotNull(transactions);
        Assert.assertFalse(transactions.isEmpty());
        Assert.assertEquals(1, transactions.size());
        Assert.assertEquals(tx.getHash(), transactions.get(0).getHash());

        Assert.assertEquals(Block.calculateTransactionsRootHash(txs), block.getTransactionRootHash());
    }

    @Test
    public void withTwoTransactionInmmutable() {
        Transaction tx1 = FactoryHelper.createTransaction(42);
        Transaction tx2 = FactoryHelper.createTransaction(100);

        List<Transaction> txs = new ArrayList<>();
        txs.add(tx1);
        txs.add(tx2);

        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(1L, hash, null, txs, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE);

        List<Transaction> transactions = block.getTransactions();

        Assert.assertEquals(Block.calculateTransactionsRootHash(txs), block.getTransactionRootHash());

        exception.expect(UnsupportedOperationException.class);
        transactions.add(FactoryHelper.createTransaction(1));
    }
}
