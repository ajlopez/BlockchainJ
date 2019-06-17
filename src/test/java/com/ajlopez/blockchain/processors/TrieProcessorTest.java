package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.KeyValueStore;
import com.ajlopez.blockchain.store.TrieStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 14/06/2019.
 */
public class TrieProcessorTest {
    @Test
    public void processEmptyTrie() {
        KeyValueStore keyValueStore = new HashMapStore();
        TrieStore trieStore = new TrieStore(keyValueStore);

        Trie trie = new Trie();

        TrieProcessor trieProcessor = new TrieProcessor(trieStore, trie.getHash());

        trieProcessor.saveNode(trie.getEncoded());

        Assert.assertTrue(trieStore.exists(trie.getHash()));
        Assert.assertNull(keyValueStore.getValue(trie.getHash().getBytes()));
        Assert.assertTrue(((HashMapStore) keyValueStore).isEmpty());
        Assert.assertTrue(trieProcessor.getPendingHashes().isEmpty());
    }

    @Test
    public void processTrieWithOneKeyValue() {
        byte[] key = FactoryHelper.createRandomBytes(32);
        byte[] value = FactoryHelper.createRandomBytes(42);

        Trie trie = new Trie().put(key, value);

        KeyValueStore keyValueStore = new HashMapStore();
        TrieStore trieStore = new TrieStore(keyValueStore);

        TrieProcessor trieProcessor = new TrieProcessor(trieStore, trie.getHash());

        trieProcessor.saveNode(trie.getEncoded());

        Assert.assertTrue(trieStore.exists(trie.getHash()));
        Assert.assertArrayEquals(trie.getEncoded(), keyValueStore.getValue(trie.getHash().getBytes()));
        Assert.assertFalse(((HashMapStore) keyValueStore).isEmpty());

        Hash[] subHashes = trie.getSubHashes();

        for (int k = 0; k < subHashes.length; k++)
            if (subHashes[k] != null)
                Assert.assertFalse(trieStore.exists(subHashes[k]));

        Assert.assertFalse(trieProcessor.getPendingHashes().isEmpty());
        Assert.assertEquals(1, trieProcessor.getPendingHashes().size());
        Assert.assertFalse(trieProcessor.getPendingHashes().contains(trie.getHash()));
    }

    @Test
    public void processUnexpectedTrie() {
        byte[] key = FactoryHelper.createRandomBytes(32);
        byte[] value = FactoryHelper.createRandomBytes(42);
        byte[] key2 = FactoryHelper.createRandomBytes(32);
        byte[] value2 = FactoryHelper.createRandomBytes(42);

        KeyValueStore keyValueStore = new HashMapStore();
        TrieStore trieStore = new TrieStore(keyValueStore);

        Trie trie = new Trie().put(key, value);
        Trie trie2 = new Trie().put(key2, value2);

        TrieProcessor trieProcessor = new TrieProcessor(trieStore, trie.getHash());

        trieProcessor.saveNode(trie.getEncoded());
        trieProcessor.saveNode(trie2.getEncoded());

        Assert.assertTrue(trieStore.exists(trie.getHash()));
        Assert.assertFalse(trieStore.exists(trie2.getHash()));
        Assert.assertArrayEquals(trie.getEncoded(), keyValueStore.getValue(trie.getHash().getBytes()));
        Assert.assertNull(keyValueStore.getValue(trie2.getHash().getBytes()));
    }
}
