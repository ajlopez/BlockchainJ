package com.ajlopez.blockchain.core.types;

import com.ajlopez.blockchain.core.types.Hash;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class HashTest {
    @Test
    public void createHash() {
        Random random = new Random();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        Hash hash = new Hash(bytes);

        Assert.assertArrayEquals(bytes, hash.getBytes());
    }

    @Test
    public void tooLargeByteArray() {
        Random random = new Random();
        byte[] bytes = new byte[33];
        random.nextBytes(bytes);

        try {
            new Hash(bytes);
            Assert.fail();
        }
        catch (IllegalArgumentException ex) {
            Assert.assertEquals("Too large byte array", ex.getMessage());
        }
    }

    @Test
    public void hashesWithTheSameBytesAreEqual() {
        Random random = new Random();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        Hash hash1 = new Hash(bytes);
        Hash hash2 = new Hash(bytes);

        Assert.assertEquals(hash1, hash2);
        Assert.assertTrue(hash1.equals(hash2));
        Assert.assertTrue(hash2.equals(hash1));
        Assert.assertEquals(hash1.hashCode(), hash2.hashCode());
    }

    @Test
    public void hashesWithTheSameBytesValuesAreEqual() {
        Random random = new Random();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        byte[] bytes2 = Arrays.copyOf(bytes, bytes.length);

        Hash hash1 = new Hash(bytes);
        Hash hash2 = new Hash(bytes2);

        Assert.assertEquals(hash1, hash2);
        Assert.assertTrue(hash1.equals(hash2));
        Assert.assertTrue(hash2.equals(hash1));
        Assert.assertEquals(hash1.hashCode(), hash2.hashCode());
    }

    @Test
    public void notEqual() {
        Random random = new Random();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        Hash hash = new Hash(bytes);

        Assert.assertFalse(hash.equals(null));
        Assert.assertFalse(hash.equals("foo"));
    }
}
