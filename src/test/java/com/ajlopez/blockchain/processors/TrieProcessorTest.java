package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.KeyValueStore;
import com.ajlopez.blockchain.store.TrieStore;
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

        TrieProcessor trieProcessor = new TrieProcessor(trieStore);

        trieProcessor.saveNode(trie.getEncoded());

        Assert.assertTrue(trieStore.exists(trie.getHash()));
        Assert.assertNull(keyValueStore.getValue(trie.getHash().getBytes()));
    }
}
