package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.KeyValueStore;
import com.ajlopez.blockchain.store.TrieStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by ajlopez on 14/06/2019.
 */
public class TrieCollectorTest {
    @Test
    public void processEmptyTrie() {
        KeyValueStore keyValueStore = new HashMapStore();
        TrieStore trieStore = new TrieStore(keyValueStore);

        Trie trie = new Trie();

        TrieCollector trieCollector = new TrieCollector(trieStore, trie.getHash());

        List<Hash> hashes = trieCollector.saveNode(trie.getEncoded());

        Assert.assertNotNull(hashes);
        Assert.assertTrue(hashes.isEmpty());

        Assert.assertTrue(trieStore.exists(trie.getHash()));
        Assert.assertNull(keyValueStore.getValue(trie.getHash().getBytes()));
        Assert.assertTrue(((HashMapStore) keyValueStore).isEmpty());
        Assert.assertTrue(trieCollector.getPendingHashes().isEmpty());
    }

    @Test
    public void processTrieWithOneKeyValue() {
        byte[] key = FactoryHelper.createRandomBytes(32);
        byte[] value = FactoryHelper.createRandomBytes(42);

        Trie trie = new Trie().put(key, value);

        KeyValueStore keyValueStore = new HashMapStore();
        TrieStore trieStore = new TrieStore(keyValueStore);

        TrieCollector trieCollector = new TrieCollector(trieStore, trie.getHash());

        List<Hash> hashes = trieCollector.saveNode(trie.getEncoded());

        Assert.assertTrue(trieStore.exists(trie.getHash()));
        Assert.assertArrayEquals(trie.getEncoded(), keyValueStore.getValue(trie.getHash().getBytes()));
        Assert.assertFalse(((HashMapStore) keyValueStore).isEmpty());

        Hash[] subHashes = trie.getSubHashes();

        for (int k = 0; k < subHashes.length; k++)
            if (subHashes[k] != null) {
                Assert.assertFalse(trieStore.exists(subHashes[k]));
                Assert.assertTrue(hashes.contains(subHashes[k]));
            }

        Assert.assertFalse(trieCollector.getPendingHashes().isEmpty());
        Assert.assertEquals(1, trieCollector.getPendingHashes().size());
        Assert.assertFalse(trieCollector.getPendingHashes().contains(trie.getHash()));
    }

    @Test
    public void processTrieWithOneKeyValueTwice() {
        byte[] key = FactoryHelper.createRandomBytes(32);
        byte[] value = FactoryHelper.createRandomBytes(42);

        Trie trie = new Trie().put(key, value);

        KeyValueStore keyValueStore = new HashMapStore();
        TrieStore trieStore = new TrieStore(keyValueStore);

        TrieCollector trieCollector = new TrieCollector(trieStore, trie.getHash());

        trieCollector.saveNode(trie.getEncoded());
        List<Hash> hashes = trieCollector.saveNode(trie.getEncoded());

        Assert.assertNotNull(hashes);
        Assert.assertTrue(hashes.isEmpty());

        Assert.assertTrue(trieStore.exists(trie.getHash()));
        Assert.assertArrayEquals(trie.getEncoded(), keyValueStore.getValue(trie.getHash().getBytes()));
        Assert.assertFalse(((HashMapStore) keyValueStore).isEmpty());

        Hash[] subHashes = trie.getSubHashes();

        for (int k = 0; k < subHashes.length; k++)
            if (subHashes[k] != null)
                Assert.assertFalse(trieStore.exists(subHashes[k]));

        Assert.assertFalse(trieCollector.getPendingHashes().isEmpty());
        Assert.assertEquals(1, trieCollector.getPendingHashes().size());
        Assert.assertFalse(trieCollector.getPendingHashes().contains(trie.getHash()));
    }

    @Test
    public void processTrieThatIsAlreadyInStore() {
        KeyValueStore keyValueStore = new HashMapStore();
        TrieStore trieStore = new TrieStore(keyValueStore);

        byte[] key = FactoryHelper.createRandomBytes(32);
        byte[] value = FactoryHelper.createRandomBytes(42);

        Trie trie = new Trie(trieStore).put(key, value);
        trie.save();

        TrieCollector trieCollector = new TrieCollector(trieStore, trie.getHash());

        Assert.assertTrue(trieStore.exists(trie.getHash()));

        Assert.assertTrue(trieCollector.getPendingHashes().isEmpty());
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

        TrieCollector trieCollector = new TrieCollector(trieStore, trie.getHash());

        trieCollector.saveNode(trie.getEncoded());
        List<Hash> hashes = trieCollector.saveNode(trie2.getEncoded());

        Assert.assertNotNull(hashes);
        Assert.assertTrue(hashes.isEmpty());

        Assert.assertTrue(trieStore.exists(trie.getHash()));
        Assert.assertFalse(trieStore.exists(trie2.getHash()));
        Assert.assertArrayEquals(trie.getEncoded(), keyValueStore.getValue(trie.getHash().getBytes()));
        Assert.assertNull(keyValueStore.getValue(trie2.getHash().getBytes()));
    }
}
