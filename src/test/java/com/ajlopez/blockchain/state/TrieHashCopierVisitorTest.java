package com.ajlopez.blockchain.state;

import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.TrieStore;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 24/10/2020.
 */
public class TrieHashCopierVisitorTest {
    @Test
    public void processEmptyTrie() throws IOException {
        TrieStore trieStore = new TrieStore(new HashMapStore());
        TrieStore targetTrieStore = new TrieStore(new HashMapStore());

        Trie trie = new Trie(trieStore);
        trie.save();

        TrieHashCopierVisitor visitor = new TrieHashCopierVisitor(trieStore, targetTrieStore);

        visitor.process(trie.getHash());

        Assert.assertTrue(targetTrieStore.exists(trie.getHash()));
    }

    @Test
    public void processTrieWithOneValue() throws IOException {
        TrieStore trieStore = new TrieStore(new HashMapStore());
        TrieStore targetTrieStore = new TrieStore(new HashMapStore());

        Trie trie = new Trie(trieStore).put(new byte[] { 0x01 }, new byte[] { 0x02 });
        trie.save();

        TrieHashCopierVisitor visitor = new TrieHashCopierVisitor(trieStore, targetTrieStore);

        visitor.process(trie.getHash());

        Assert.assertTrue(targetTrieStore.exists(trie.getHash()));
    }

    @Test
    public void processTrieWithTwoValues() throws IOException {
        TrieStore trieStore = new TrieStore(new HashMapStore());
        TrieStore targetTrieStore = new TrieStore(new HashMapStore());

        Trie trie = new Trie(trieStore)
            .put(new byte[] { 0x01 }, new byte[] { 0x02 })
            .put(new byte[] { 0x02 }, new byte[] { 0x03 });
        trie.save();

        TrieHashCopierVisitor visitor = new TrieHashCopierVisitor(trieStore, targetTrieStore);

        visitor.process(trie.getHash());

        Assert.assertTrue(targetTrieStore.exists(trie.getHash()));
    }
}
