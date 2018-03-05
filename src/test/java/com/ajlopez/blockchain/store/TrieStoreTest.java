package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Created by ajlopez on 04/03/2018.
 */
public class TrieStoreTest {
    private static Random random = new Random();

    @Test
    public void retrieveNoTrie() {
        TrieStore store = new TrieStore(new HashMapStore());

        Assert.assertNull(store.retrieve(HashUtilsTest.generateRandomHash()));
    }

    @Test
    public void saveAndRetrieveEmptyTrie() {
        TrieStore store = new TrieStore(new HashMapStore());
        Trie trie = Trie.getEmptyTrie();

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

        Trie trie = Trie.getEmptyTrie().put(key, value);

        store.save(trie);

        Trie result = store.retrieve(trie.getHash());

        Assert.assertNotNull(result);
        Assert.assertEquals(trie.getHash(), result.getHash());
    }
}
