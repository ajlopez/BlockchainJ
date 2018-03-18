package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Random;

/**
 * Created by ajlopez on 04/03/2018.
 */
public class TrieStoreTest {
    private static Random random = new Random();

    @Test(expected = RuntimeException.class)
    public void retrieveUnknownTrie() {
        TrieStore store = new TrieStore(new HashMapStore());

        store.retrieve(HashUtilsTest.generateRandomHash());
    }

    @Test
    public void saveAndRetrieveEmptyTrie() {
        TrieStore store = new TrieStore(new HashMapStore());
        Trie trie = new Trie();

        store.save(trie);

        Trie result = store.retrieve(trie.getHash());

        Assert.assertNotNull(result);
        Assert.assertEquals(trie.getHash(), result.getHash());
    }

    @Test
    public void saveAndRetrieveTrieWithKeyValue() {
        byte[] value = new byte[32];
        random.nextBytes(value);
        byte[] key = new byte[2];

        TrieStore store = new TrieStore(new HashMapStore());

        Trie trie = new Trie(store).put(key, value);

        trie.save();

        Trie result = store.retrieve(trie.getHash());

        Assert.assertNotNull(result);
        Assert.assertEquals(trie.getHash(), result.getHash());

        byte[] rvalue = result.get(key);

        Assert.assertNotNull(rvalue);
        Assert.assertArrayEquals(rvalue, value);
    }

    @Test
    public void saveAndRetrieveTrieWithOneHundredKeyValues() {
        byte[][] values = new byte[100][];
        byte[][] keys = new byte[100][];

        for (int k = 0; k < 100; k++) {
            keys[k] = new byte[1];
            keys[k][0] = (byte)k;

            values[k] = new byte[32];
            random.nextBytes(values[k]);
        }

        TrieStore store = new TrieStore(new HashMapStore());

        Trie trie = new Trie(store);

        for (int k = 0; k < 100; k++)
            trie = trie.put(keys[k], values[k]);

        trie.save();

        Trie result = store.retrieve(trie.getHash());

        Assert.assertNotNull(result);
        Assert.assertEquals(trie.getHash(), result.getHash());

        for (int k = 0; k < 100; k++)
            Assert.assertArrayEquals(values[k], result.get(keys[k]));
    }
}
