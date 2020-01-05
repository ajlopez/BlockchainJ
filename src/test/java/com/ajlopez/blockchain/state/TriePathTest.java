package com.ajlopez.blockchain.state;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 05/01/2020.
 */
public class TriePathTest {
    @Test
    public void createEmptyTriePath() {
        TriePath triePath = new TriePath();

        Assert.assertEquals(0, triePath.getSize());
    }

    @Test
    public void createTriePathWithOnlyOneTrie() {
        TriePath triePath = new TriePath();
        Trie trie = new Trie();

        triePath.addLastTrie(trie);

        Assert.assertEquals(1, triePath.getSize());
        Assert.assertSame(trie, triePath.getTrie(0));
    }

    @Test
    public void createTriePathWithTwoTries() {
        TriePath triePath = new TriePath();
        Trie trie1 = new Trie();
        Trie trie2 = new Trie();

        triePath.addTrieAndChildPosition(trie1, 1);
        triePath.addLastTrie(trie2);

        Assert.assertEquals(2, triePath.getSize());
        Assert.assertSame(trie1, triePath.getTrie(0));
        Assert.assertSame(trie2, triePath.getTrie(1));
        Assert.assertEquals(1, triePath.getChildPosition(0));
    }
}
