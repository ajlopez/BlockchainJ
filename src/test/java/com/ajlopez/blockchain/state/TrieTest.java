package com.ajlopez.blockchain.state;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.utils.HashUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class TrieTest {
    private static Random random = new Random();

    @Test
    public void getUnknownValueAsNull() {
        Trie trie = new Trie();

        Assert.assertNull(trie.get(new byte[] { 0x01, 0x02 }));
        Assert.assertEquals(1, trie.nodesSize());
    }

    @Test
    public void getEncodedEmptyTrie() {
        Trie trie = new Trie();

        byte[] encoded = trie.getEncoded();

        Assert.assertNotNull(encoded);
        Assert.assertEquals(5, encoded.length);

        byte[] expected = new byte[5];
        expected[1] = 16; // arity

        Assert.assertArrayEquals(expected, encoded);
    }

    @Test
    public void getEncodedTrieWithValueAndNoSubNodes() {
        byte[] value = new byte[32];
        random.nextBytes(value);

        Trie trie = new Trie().put(new byte[0], value);

        byte[] encoded = trie.getEncoded();

        Assert.assertNotNull(encoded);
        Assert.assertEquals(5 + Integer.BYTES + 32, encoded.length);

        byte[] expected = new byte[5 + Integer.BYTES + 32];
        expected[1] = 16; // arity
        expected[2] = Integer.BYTES; // value length in bytes
        expected[8] = 32; // value length in bytes[5..8]
        System.arraycopy(value, 0, expected, 9, value.length);

        Assert.assertArrayEquals(expected, encoded);
    }

    @Test
    public void getEncodedTrieWithoutValueAndSubNode() {
        byte[] value = new byte[32];
        random.nextBytes(value);

        Trie trie = new Trie().put(new byte[] { 0x01 }, value);

        byte[] encoded = trie.getEncoded();

        Assert.assertNotNull(encoded);
        Assert.assertEquals(5 + HashUtils.HASH_BYTES, encoded.length);

        byte[] firstexpected = new byte[5];
        firstexpected[1] = 16; // arity
        firstexpected[4] = 1; // first subnode

        Assert.assertArrayEquals(firstexpected, Arrays.copyOfRange(encoded, 0, 5));
    }

    @Test
    public void retrieveTrieFromEncodedEmptyTrie() {
        Trie trie = new Trie();

        byte[] encoded = trie.getEncoded();

        Trie result = Trie.fromEncoded(encoded, null);

        Assert.assertNotNull(result);
        Assert.assertEquals(trie.getHash(), result.getHash());
    }

    @Test
    public void retrieveFromEncodedTrieWithKeyValueInSubTrie() {
        byte[] value = new byte[32];
        random.nextBytes(value);
        byte[] key = new byte[] { 0x01 };
        Trie trie = new Trie().put(key, value);

        byte[] encoded = trie.getEncoded();

        Trie result = Trie.fromEncoded(encoded, null);

        Assert.assertNotNull(result);
        Assert.assertEquals(trie.getHash(), result.getHash());
    }

    @Test
    public void retrieveTrieFromEncodedTrieWithValue() {
        byte[] value = new byte[32];
        random.nextBytes(value);
        byte[] key = new byte[0];
        Trie trie = new Trie().put(key, value);

        byte[] encoded = trie.getEncoded();

        Trie result = Trie.fromEncoded(encoded, null);

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(encoded, result.getEncoded());
        Assert.assertEquals(trie.getHash(), result.getHash());
        Assert.assertArrayEquals(value, trie.get(key));
    }

    @Test
    public void getHashFromEmptyTrie() {
        Trie trie = new Trie();

        Hash hash = trie.getHash();

        Assert.assertNotNull(hash);
    }

    @Test
    public void getHashFromTrieWithValueAndNoSubNodes() {
        byte[] value = new byte[32];
        random.nextBytes(value);

        Trie trie = new Trie().put(new byte[0], value);

        Hash hash = trie.getHash();

        Assert.assertNotNull(hash);

        Trie trie2 = new Trie().put(new byte[0], value);

        Hash hash2 = trie2.getHash();

        Assert.assertEquals(hash, hash2);
    }

    @Test
    public void getUnknownValueWithEmptyKeyAsNull() {
        Trie trie = new Trie();

        Assert.assertNull(trie.get(new byte[0]));
        Assert.assertEquals(1, trie.nodesSize());
    }

    @Test
    public void putAndGetKeyValue() {
        byte[] key = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value = new byte[] { 0x01, 0x02, 0x03 };

        Trie trie = new Trie();
        trie = trie.put(key, value);

        Assert.assertNotNull(trie);
        Assert.assertArrayEquals(value, trie.get(key));
        Assert.assertEquals(5, trie.nodesSize());
    }

    @Test
    public void putAndGetKeyNullValue() {
        byte[] key = new byte[0];
        byte[] value = new byte[] { 0x01, 0x02, 0x03 };

        Trie trie = new Trie();
        trie = trie.put(key, value);

        Assert.assertNotNull(trie);
        Assert.assertArrayEquals(value, trie.get(key));
        Assert.assertEquals(1, trie.nodesSize());
    }

    @Test
    public void putRemoveAndGetKeyValue() {
        byte[] key = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value = new byte[] { 0x01, 0x02, 0x03 };

        Trie trie = new Trie();
        trie = trie.put(key, value).delete(key);
        Assert.assertNotNull(trie);
        Assert.assertNull(trie.get(key));
        Assert.assertEquals(1, trie.nodesSize());
        Assert.assertEquals((new Trie()).getHash(), trie.getHash());
    }
    
    @Test
    public void putRemoveAndGetTwoKeyValue() {
        byte[] key1 = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value1 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] key2 = new byte[] { (byte)0xab, (byte)0xce };
        byte[] value2 = new byte[] { 0x01, 0x02, 0x03, 0x04 };

        Trie trie = new Trie();
        trie = trie.put(key1, value1)
                .put(key2, value2)
                .delete(key1)
                .delete(key2);

        Assert.assertNotNull(trie);
        Assert.assertNull(trie.get(key1));
        Assert.assertNull(trie.get(key2));
        Assert.assertEquals(1, trie.nodesSize());
        Assert.assertEquals((new Trie()).getHash(), trie.getHash());
    }

    @Test
    public void putAndGetTwoKeyValues() {
        byte[] key1 = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value1 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] key2 = new byte[] { (byte)0xcd, (byte)0xab };
        byte[] value2 = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        byte[] key3 = new byte[] { (byte)0xcd, (byte)0xaa };

        Trie trie = new Trie().put(key1, value1).put(key2, value2);

        Assert.assertNotNull(trie);
        Assert.assertArrayEquals(value1, trie.get(key1));
        Assert.assertArrayEquals(value2, trie.get(key2));
        Assert.assertNull(trie.get(key3));
        Assert.assertEquals(9, trie.nodesSize());
    }

    @Test
    public void putAndGetTwoKeyValuesSamePath() {
        byte[] key1 = new byte[] { (byte)0xab };
        byte[] value1 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] key2 = new byte[] { (byte)0xab, (byte)0xab };
        byte[] value2 = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        byte[] key3 = new byte[] { (byte)0xcd, (byte)0xaa };

        Trie trie = new Trie().put(key1, value1).put(key2, value2);

        Assert.assertNotNull(trie);
        Assert.assertArrayEquals(value1, trie.get(key1));
        Assert.assertArrayEquals(value2, trie.get(key2));
        Assert.assertNull(trie.get(key3));
        Assert.assertEquals(5, trie.nodesSize());
    }

    @Test
    public void putChangeAndGetKeyValue() {
        byte[] key1 = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value1 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] value2 = new byte[] { 0x01, 0x02, 0x03, 0x04 };

        Trie trie = new Trie().put(key1, value1).put(key1, value2);

        Assert.assertNotNull(trie);
        Assert.assertArrayEquals(value2, trie.get(key1));
        Assert.assertEquals(5, trie.nodesSize());
    }
}
