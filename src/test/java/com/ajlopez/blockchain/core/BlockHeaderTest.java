package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class BlockHeaderTest {
    @Test
    public void createWithNumberAndParentHash() {
        BlockHash hash = new BlockHash(HashUtilsTest.generateRandomHash());
        Hash transactionsHash = HashUtilsTest.generateRandomHash();
        Hash stateRootHash = HashUtilsTest.generateRandomHash();
        BlockHeader header = new BlockHeader(1L, hash, transactionsHash, stateRootHash);

        Assert.assertEquals(1L, header.getNumber());
        Assert.assertEquals(hash, header.getParentHash());
        Assert.assertEquals(transactionsHash, header.getTransactionsHash());
        Assert.assertNotNull(header.getHash());
    }

    @Test
    public void twoDifferentHeadersHaveDifferentHashes() {
        BlockHeader header1 = new BlockHeader(1L, new BlockHash(HashUtilsTest.generateRandomHash()), HashUtilsTest.generateRandomHash(), HashUtilsTest.generateRandomHash());
        BlockHeader header2 = new BlockHeader(2L, new BlockHash(HashUtilsTest.generateRandomHash()), HashUtilsTest.generateRandomHash(), HashUtilsTest.generateRandomHash());

        Assert.assertNotEquals(header1.getHash(), header2.getHash());
    }

    @Test(expected = IllegalStateException.class)
    public void negativeNumber() {
        BlockHash hash = new BlockHash(HashUtilsTest.generateRandomHash());
        Hash transactionsHash = HashUtilsTest.generateRandomHash();
        Hash stateRootHash = HashUtilsTest.generateRandomHash();

        new BlockHeader(-1L, hash, transactionsHash, stateRootHash);
    }
}
