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

    @Test
    public void getSubKey() {
        byte[] key = { 0x12, 0x34, (byte)0xfe };

        byte[] skey01 = { 0x10 };
        byte[] skey02 = { 0x12 };
        byte[] skey11 = { 0x20 };
        byte[] skey12 = { 0x23 };
        byte[] skey33 = { 0x4f, (byte)0xe0 };

        Assert.assertArrayEquals(skey01, TrieKeyUtils.getSubKey(key, 0, 1));
        Assert.assertArrayEquals(skey02, TrieKeyUtils.getSubKey(key, 0, 2));
        Assert.assertArrayEquals(skey11, TrieKeyUtils.getSubKey(key, 1, 1));
        Assert.assertArrayEquals(skey12, TrieKeyUtils.getSubKey(key, 1, 2));
        Assert.assertArrayEquals(skey33, TrieKeyUtils.getSubKey(key, 3, 3));
    }
}
