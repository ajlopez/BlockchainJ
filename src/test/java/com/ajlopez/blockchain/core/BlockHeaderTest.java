package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class BlockHeaderTest {
    @Test
    public void createWithNumberAndParentHash() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Hash transactionsHash = FactoryHelper.createRandomHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();
        Difficulty difficulty = Difficulty.fromUnsignedLong(42);

        BlockHeader header = new BlockHeader(1L, hash, 42, transactionsHash, null, 0, null, stateRootHash, System.currentTimeMillis() / 1000, coinbase, difficulty, 0);

        Assert.assertEquals(1L, header.getNumber());
        Assert.assertEquals(hash, header.getParentHash());
        Assert.assertEquals(42, header.getTransactionsCount());
        Assert.assertEquals(transactionsHash, header.getTransactionsRootHash());
        Assert.assertNotNull(header.getHash());
        Assert.assertEquals(Difficulty.fromUnsignedLong(42), header.getDifficulty());
        Assert.assertEquals(0, header.getNonce());
    }

    @Test
    public void createWithNonce() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Hash transactionsHash = FactoryHelper.createRandomHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();
        Difficulty difficulty = Difficulty.fromUnsignedLong(42);

        BlockHeader header = new BlockHeader(1L, hash, 42, transactionsHash, null, 0, null, stateRootHash, System.currentTimeMillis() / 1000, coinbase, difficulty, 42);

        Assert.assertEquals(1L, header.getNumber());
        Assert.assertEquals(hash, header.getParentHash());
        Assert.assertEquals(42, header.getTransactionsCount());
        Assert.assertEquals(transactionsHash, header.getTransactionsRootHash());
        Assert.assertNotNull(header.getHash());
        Assert.assertEquals(Difficulty.fromUnsignedLong(42), header.getDifficulty());
        Assert.assertEquals(42, header.getNonce());
    }

    @Test
    public void equals() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Hash transactionsHash = FactoryHelper.createRandomHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();
        Difficulty difficulty = Difficulty.fromUnsignedLong(42);

        BlockHeader header = new BlockHeader(1L, hash, 0, transactionsHash, null, 0, null, stateRootHash, 1L, coinbase, difficulty, 0);
        BlockHeader header2 = new BlockHeader(1L, hash, 0, transactionsHash, null, 0, null, stateRootHash, 1L, coinbase, difficulty, 0);
        BlockHeader header3 = new BlockHeader(1L, hash, 0, transactionsHash, null, 0, null, stateRootHash, 2L, coinbase, difficulty, 0);

        Assert.assertTrue(header.equals(header2));
        Assert.assertTrue(header2.equals(header));
        Assert.assertFalse(header.equals(header3));
        Assert.assertFalse(header2.equals(header3));
        Assert.assertFalse(header3.equals(header));
        Assert.assertFalse(header3.equals(header2));

        Assert.assertFalse(header.equals(null));
        Assert.assertFalse(header.equals("foo"));

        Assert.assertEquals(header.hashCode(), header2.hashCode());
        Assert.assertNotEquals(header.hashCode(), header3.hashCode());
    }

    @Test
    public void twoDifferentHeadersHaveDifferentHashes() {
        BlockHeader header1 = new BlockHeader(1L, FactoryHelper.createRandomBlockHash(), 0, FactoryHelper.createRandomHash(), null, 0, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), Difficulty.fromUnsignedLong(42), 0);
        BlockHeader header2 = new BlockHeader(2L, FactoryHelper.createRandomBlockHash(), 0, FactoryHelper.createRandomHash(), null, 0, null, FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), Difficulty.fromUnsignedLong(42), 0);

        Assert.assertNotEquals(header1.getHash(), header2.getHash());
    }

    @Test(expected = IllegalStateException.class)
    public void negativeNumber() {
        BlockHash hash = FactoryHelper.createRandomBlockHash();
        Hash transactionsHash = FactoryHelper.createRandomHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();

        new BlockHeader(-1L, hash, 0, transactionsHash, null, 0, null, stateRootHash, System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), null, 0);
    }
}
