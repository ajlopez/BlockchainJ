package com.ajlopez.blockchain.store;

import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.utils.HashUtilsTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 04/03/2018.
 */
public class TrieStoreTest {
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
}
