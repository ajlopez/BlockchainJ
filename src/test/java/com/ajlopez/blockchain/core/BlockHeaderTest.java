package com.ajlopez.blockchain.core;

import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class BlockHeaderTest {
    @Test
    public void createWithNumberAndParentHash() {
        Hash hash = HashUtilsTest.generateRandomHash();
        Hash transactionsHash = HashUtilsTest.generateRandomHash();
        BlockHeader header = new BlockHeader(1L, hash, transactionsHash);

        Assert.assertEquals(1L, header.getNumber());
        Assert.assertEquals(hash, header.getParentHash());
        Assert.assertEquals(transactionsHash, header.getTransactionsHash());
        Assert.assertNotNull(header.getHash());
    }

    @Test
    public void twoDifferentHeadersHaveDifferentHashes() {
        BlockHeader header1 =
                new BlockHeader(1L, HashUtilsTest.generateRandomHash(), HashUtilsTest.generateRandomHash());
        BlockHeader header2 = new BlockHeader(2L, HashUtilsTest.generateRandomHash(), HashUtilsTest.generateRandomHash());

        Assert.assertNotEquals(header1.getHash(), header2.getHash());
    }

    @Test(expected = IllegalStateException.class)
    public void negativeNumber() {
        Hash hash = HashUtilsTest.generateRandomHash();
        Hash transactionsHash = HashUtilsTest.generateRandomHash();

        new BlockHeader(-1L, hash, transactionsHash);
    }
}
