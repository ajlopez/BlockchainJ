package com.ajlopez.blockchain.state;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 22/10/2020.
 */
public class TrieNodeStatsVisitorTest {
    @Test
    public void processEmptyTrie() throws IOException {
        Trie trie = new Trie();

        TrieNodeStatsVisitor visitor = new TrieNodeStatsVisitor();

        visitor.process(trie);

        Assert.assertEquals(1, visitor.getNodeCounter());
        Assert.assertEquals(0, visitor.getValueCounter());
        Assert.assertEquals(trie.getEncoded().length, visitor.getEncodedSize());
    }

    @Test
    public void processTrieWithOneValue() throws IOException {
        Trie trie = new Trie().put(new byte[] { 0x01 }, new byte[] { 0x02 });

        TrieNodeStatsVisitor visitor = new TrieNodeStatsVisitor();

        visitor.process(trie);

        Assert.assertEquals(1, visitor.getNodeCounter());
        Assert.assertEquals(1, visitor.getValueCounter());
        Assert.assertEquals(trie.getEncoded().length, visitor.getEncodedSize());
    }

    @Test
    public void processTrieWithTwoValues() throws IOException {
        Trie trie = new Trie()
            .put(new byte[] { 0x01 }, new byte[] { 0x02 })
            .put(new byte[] { 0x02 }, new byte[] { 0x03 });

        TrieNodeStatsVisitor visitor = new TrieNodeStatsVisitor();

        visitor.process(trie);

        Assert.assertEquals(3, visitor.getNodeCounter());
        Assert.assertEquals(2, visitor.getValueCounter());
        Assert.assertTrue(trie.getEncoded().length < visitor.getEncodedSize());
    }
}
