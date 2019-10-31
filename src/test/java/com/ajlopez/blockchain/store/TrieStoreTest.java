package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

/**
 * Created by ajlopez on 04/03/2018.
 */
public class TrieStoreTest {
    private static Random random = new Random();

    @Test(expected = RuntimeException.class)
    public void retrieveUnknownTrie() throws IOException {
        TrieStore store = new TrieStore(new HashMapStore());

        store.retrieve(FactoryHelper.createRandomHash());
    }

    @Test
    public void saveAndRetrieveEmptyTrie() throws IOException {
        TrieStore store = new TrieStore(new HashMapStore());
        Trie trie = new Trie();

        store.save(trie);

        Trie result = store.retrieve(trie.getHash());

        Assert.assertNotNull(result);
        Assert.assertEquals(trie.getHash(), result.getHash());
    }

    @Test
    public void retrieveEmptyTrieIfHashIsNull() throws IOException {
        TrieStore store = new TrieStore(new HashMapStore());
        Trie trie = store.retrieve(null);

        Assert.assertNotNull(trie);
        Assert.assertEquals(Trie.EMPTY_TRIE_HASH, trie.getHash());
    }

    @Test
    public void saveAndRetrieveTrieWithKeyValue() throws IOException {
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
    public void saveAndRetrieveTrieWithOneHundredKeyValues() throws IOException {
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

    @Test
    public void unknownHashDoesNotExists() throws IOException {
        TrieStore store = new TrieStore(new HashMapStore());

        Assert.assertFalse(store.exists(FactoryHelper.createRandomHash()));
    }

    @Test
    public void knownHashExists() throws IOException {
        TrieStore store = new TrieStore(new HashMapStore());

        byte[] value = new byte[32];
        random.nextBytes(value);
        byte[] key = new byte[2];

        Trie trie = new Trie(store).put(key, value);

        trie.save();

        Assert.assertTrue(store.exists(trie.getHash()));
    }

    @Test
    public void emptyTryExists() throws IOException {
        TrieStore store = new TrieStore(new HashMapStore());

        Assert.assertTrue(store.exists(null));
        Assert.assertTrue(store.exists(Trie.EMPTY_TRIE_HASH));
    }
}
