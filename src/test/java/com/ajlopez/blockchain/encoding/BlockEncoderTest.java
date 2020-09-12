package com.ajlopez.blockchain.encoding;

import com.ajlopez.blockchain.core.*;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ajlopez on 10/10/2017.
 */
public class BlockEncoderTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void encodeDecodeBlock() {
        BlockHash parentHash = FactoryHelper.createRandomBlockHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(42, parentHash, null, stateRootHash, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0);

        byte[] encoded = BlockEncoder.encode(block);

        Assert.assertNotNull(encoded);

        Block result = BlockEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getNumber(), result.getNumber());
        Assert.assertEquals(block.getParentHash(), result.getParentHash());

        Assert.assertNotNull(result.getUncles());
        Assert.assertTrue(result.getUncles().isEmpty());
    }

    @Test
    public void encodeDecodeBlockWithReceiptsRootHash() {
        BlockHash parentHash = FactoryHelper.createRandomBlockHash();
        Hash receiptsRootHash = FactoryHelper.createRandomHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(42, parentHash, receiptsRootHash, stateRootHash, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0);

        byte[] encoded = BlockEncoder.encode(block);

        Assert.assertNotNull(encoded);

        Block result = BlockEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getNumber(), result.getNumber());
        Assert.assertEquals(block.getParentHash(), result.getParentHash());
        Assert.assertEquals(receiptsRootHash, result.getReceiptsRootHash());

        Assert.assertNotNull(result.getUncles());
        Assert.assertTrue(result.getUncles().isEmpty());
    }

    @Test
    public void encodeDecodeBlockWithOneTransaction() {
        Transaction tx = FactoryHelper.createTransaction(42);

        List<Transaction> txs = new ArrayList<>();
        txs.add(tx);

        BlockHash parentHash = FactoryHelper.createRandomBlockHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(42, parentHash, null, txs, null, stateRootHash, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0);

        byte[] encoded = BlockEncoder.encode(block);

        Assert.assertNotNull(encoded);

        Block result = BlockEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getNumber(), result.getNumber());
        Assert.assertEquals(block.getParentHash(), result.getParentHash());
        Assert.assertEquals(1, result.getTransactionsCount());
        Assert.assertNotNull(result.getTransactions());
        Assert.assertEquals(1, result.getTransactions().size());
        Assert.assertEquals(tx.getSender(), result.getTransactions().get(0).getSender());
        Assert.assertEquals(tx.getReceiver(), result.getTransactions().get(0).getReceiver());
        Assert.assertEquals(tx.getValue(), result.getTransactions().get(0).getValue());
        Assert.assertEquals(tx.getHash(), result.getTransactions().get(0).getHash());
    }

    @Test
    public void encodeDecodeBlockWithTwoTransactions() {
        Transaction tx1 = FactoryHelper.createTransaction(42);
        Transaction tx2 = FactoryHelper.createTransaction(144);

        List<Transaction> txs = new ArrayList<>();
        txs.add(tx1);
        txs.add(tx2);

        BlockHash parentHash = FactoryHelper.createRandomBlockHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(42, parentHash, null, txs, null, stateRootHash, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0);

        byte[] encoded = BlockEncoder.encode(block);

        Assert.assertNotNull(encoded);

        Block result = BlockEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getNumber(), result.getNumber());
        Assert.assertEquals(block.getParentHash(), result.getParentHash());
        Assert.assertEquals(2, result.getTransactionsCount());
        Assert.assertNotNull(result.getTransactions());
        Assert.assertEquals(2, result.getTransactions().size());
        Assert.assertEquals(0, result.getUnclesCount());
        Assert.assertEquals(block.getUnclesRootHash(), result.getUnclesRootHash());

        Assert.assertEquals(tx1.getSender(), result.getTransactions().get(0).getSender());
        Assert.assertEquals(tx1.getReceiver(), result.getTransactions().get(0).getReceiver());
        Assert.assertEquals(tx1.getValue(), result.getTransactions().get(0).getValue());
        Assert.assertEquals(tx1.getNonce(), result.getTransactions().get(0).getNonce());
        Assert.assertEquals(tx1.getHash(), result.getTransactions().get(0).getHash());

        Assert.assertEquals(tx2.getSender(), result.getTransactions().get(1).getSender());
        Assert.assertEquals(tx2.getReceiver(), result.getTransactions().get(1).getReceiver());
        Assert.assertEquals(tx2.getValue(), result.getTransactions().get(1).getValue());
        Assert.assertEquals(tx2.getNonce(), result.getTransactions().get(1).getNonce());
        Assert.assertEquals(tx2.getHash(), result.getTransactions().get(1).getHash());
    }

    @Test
    public void encodeDecodeBlockWithTwoUncles() {
        BlockHeader uncle1 = FactoryHelper.createBlockHeader(1);
        BlockHeader uncle2 = FactoryHelper.createBlockHeader(1);

        List<BlockHeader> uncles = new ArrayList<>();
        uncles.add(uncle1);
        uncles.add(uncle2);

        BlockHash parentHash = FactoryHelper.createRandomBlockHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block = new Block(42, parentHash, uncles, null, null, stateRootHash, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0);

        byte[] encoded = BlockEncoder.encode(block);

        Assert.assertNotNull(encoded);

        Block result = BlockEncoder.decode(encoded);

        Assert.assertNotNull(result);
        Assert.assertEquals(block.getNumber(), result.getNumber());
        Assert.assertEquals(block.getParentHash(), result.getParentHash());
        Assert.assertEquals(0, result.getTransactionsCount());
        Assert.assertEquals(block.getTransactionsRootHash(), result.getTransactionsRootHash());
        Assert.assertNotNull(result.getUncles());
        Assert.assertEquals(2, result.getUncles().size());
        Assert.assertEquals(block.getUnclesRootHash(), result.getUnclesRootHash());

        Assert.assertEquals(uncle1, result.getUncles().get(0));
        Assert.assertEquals(uncle2, result.getUncles().get(1));
    }

    @Test
    public void encodeTwoBlocks() {
        BlockHash parentHash = FactoryHelper.createRandomBlockHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        Block block1 = new Block(42, parentHash, null, stateRootHash, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0);
        Block block2 = new Block(0, null, null, stateRootHash, System.currentTimeMillis() / 1000, coinbase, Difficulty.ONE, 0, 0);

        byte[] encoded1 = BlockEncoder.encode(block1);
        byte[] encoded2 = BlockEncoder.encode(block2);

        Assert.assertNotNull(encoded1);
        Assert.assertNotNull(encoded2);

        Assert.assertFalse(Arrays.equals(encoded1, encoded2));
    }

    @Test
    public void decodeInvalidEncodedBlock() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Hash transactionsHash = FactoryHelper.createRandomHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();
        BlockHeader header = new BlockHeader(42, hash, 100, transactionsHash, null, 0, null, stateRootHash, System.currentTimeMillis() / 1000, coinbase, Difficulty.fromUnsignedLong(42), 0, 0, 0);

        byte[] encodedHeader = BlockHeaderEncoder.encode(header);
        byte[] bytes = FactoryHelper.createRandomBytes(42);
        byte[] encoded = RLP.encodeList(encodedHeader, RLP.encode(bytes));

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid block encoding");
        BlockEncoder.decode(encoded);
    }
}
