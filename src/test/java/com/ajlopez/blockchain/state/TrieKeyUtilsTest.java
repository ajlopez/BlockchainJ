package com.ajlopez.blockchain.state;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 18/01/2020.
 */
public class TrieKeyUtilsTest {
    @Test
    public void getOffset() {
        byte[] key = { 0x12, 0x34, (byte)0xfe };

        Assert.assertEquals(1, TrieKeyUtils.getOffset(key, 0));
        Assert.assertEquals(2, TrieKeyUtils.getOffset(key, 1));
        Assert.assertEquals(3, TrieKeyUtils.getOffset(key, 2));
        Assert.assertEquals(4, TrieKeyUtils.getOffset(key, 3));
        Assert.assertEquals(15, TrieKeyUtils.getOffset(key, 4));
        Assert.assertEquals(14, TrieKeyUtils.getOffset(key, 5));
    }
}
