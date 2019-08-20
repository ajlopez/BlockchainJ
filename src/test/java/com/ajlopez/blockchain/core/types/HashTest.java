package com.ajlopez.blockchain.core.types;

import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HexUtils;
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
        byte[] bytes = new byte[Hash.HASH_BYTES];
        random.nextBytes(bytes);

        Hash hash = new Hash(bytes);

        Assert.assertArrayEquals(bytes, hash.getBytes());
    }

    @Test
    public void hashToString() {
        Random random = new Random();
        byte[] bytes = new byte[Hash.HASH_BYTES];
        random.nextBytes(bytes);

        Hash hash = new Hash(bytes);

        String expected = HexUtils.bytesToHexString(bytes, true);
        Assert.assertEquals(expected, hash.toString());
    }

    @Test
    public void tooLargeByteArray() {
        Random random = new Random();
        byte[] bytes = new byte[Hash.HASH_BYTES + 1];
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
    public void nullByteArrayInConstructor() {
        try {
            new Hash(null);
            Assert.fail();
        }
        catch (IllegalArgumentException ex) {
            Assert.assertEquals("Null byte array", ex.getMessage());
        }
    }

    @Test
    public void hashesWithTheSameBytesAreEqual() {
        Random random = new Random();
        byte[] bytes = new byte[Hash.HASH_BYTES];
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
        byte[] bytes = new byte[Hash.HASH_BYTES];
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
        byte[] bytes = new byte[Hash.HASH_BYTES];
        random.nextBytes(bytes);

        Hash hash = new Hash(bytes);
        DataWord dataWord = new DataWord(bytes);

        Assert.assertFalse(hash.equals(null));
        Assert.assertFalse(hash.equals(dataWord));
        Assert.assertFalse(dataWord.equals(hash));
        Assert.assertFalse(hash.equals("foo"));
        Assert.assertFalse(hash.equals(new BlockHash(bytes)));
    }

    @Test
    public void asLong() {
        byte[] bytes = FactoryHelper.createRandomBytes(Hash.HASH_BYTES);

        Hash hash = new Hash(bytes);

        Assert.assertEquals(ByteUtils.bytesToLong(bytes, Hash.HASH_BYTES - Long.BYTES), hash.asLong());
    }
}
