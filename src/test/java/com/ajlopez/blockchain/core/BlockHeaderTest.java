package com.ajlopez.blockchain.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class BlockHeaderTest {
    @Test
    public void createWithNumberAndParentHash() {
        Hash hash = generateHash();
        BlockHeader header = new BlockHeader(1L, hash);

        Assert.assertEquals(1L, header.getNumber());
        Assert.assertEquals(hash, header.getParentHash());
        Assert.assertNotNull(header.getHash());
    }

    @Test
    public void twoDifferentHeadersHaveDifferentHashes() {
        BlockHeader header1 = new BlockHeader(1L, generateHash());
        BlockHeader header2 = new BlockHeader(2L, generateHash());

        Assert.assertNotEquals(header1.getHash(), header2.getHash());
    }

    private static Hash generateHash() {
        byte[] bytes = new byte[32];
        Random random = new Random();
        random.nextBytes(bytes);
        return new Hash(bytes);
    }
}
