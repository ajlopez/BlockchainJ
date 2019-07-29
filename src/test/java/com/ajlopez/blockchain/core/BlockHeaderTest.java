package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.BlockHash;
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
        BlockHash hash = new BlockHash(FactoryHelper.createRandomHash());
        Hash transactionsHash = FactoryHelper.createRandomHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();
        Address coinbase = FactoryHelper.createRandomAddress();

        BlockHeader header = new BlockHeader(1L, hash, transactionsHash, stateRootHash, System.currentTimeMillis() / 1000, coinbase);

        Assert.assertEquals(1L, header.getNumber());
        Assert.assertEquals(hash, header.getParentHash());
        Assert.assertEquals(transactionsHash, header.getTransactionsHash());
        Assert.assertNotNull(header.getHash());
    }

    @Test
    public void twoDifferentHeadersHaveDifferentHashes() {
        BlockHeader header1 = new BlockHeader(1L, new BlockHash(FactoryHelper.createRandomHash()), FactoryHelper.createRandomHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress());
        BlockHeader header2 = new BlockHeader(2L, new BlockHash(FactoryHelper.createRandomHash()), FactoryHelper.createRandomHash(), FactoryHelper.createRandomHash(), System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress());

        Assert.assertNotEquals(header1.getHash(), header2.getHash());
    }

    @Test(expected = IllegalStateException.class)
    public void negativeNumber() {
        BlockHash hash = new BlockHash(FactoryHelper.createRandomHash());
        Hash transactionsHash = FactoryHelper.createRandomHash();
        Hash stateRootHash = FactoryHelper.createRandomHash();

        new BlockHeader(-1L, hash, transactionsHash, stateRootHash, System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress());
    }
}
