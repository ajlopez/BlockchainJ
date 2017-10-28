package com.ajlopez.blockchain.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class BlockTest {
    @Test
    public void createWithNumberAndParentHash() {
        Hash hash = generateHash();
        Block block = new Block(1L, hash);

        Assert.assertEquals(1L, block.getNumber());
        Assert.assertEquals(hash, block.getParentHash());
        Assert.assertNotNull(block.getHash());
    }

    private static Hash generateHash() {
        byte[] bytes = new byte[32];
        Random random = new Random();
        random.nextBytes(bytes);
        return new Hash(bytes);
    }
}
