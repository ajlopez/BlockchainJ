package com.ajlopez.blockchain.state;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class TrieTest {
    @Test
    public void getUnknownValueAsNull() {
        Trie trie = Trie.getEmptyTrie();

        Assert.assertNull(trie.get(new byte[] { 0x01, 0x02 }));
        Assert.assertEquals(1, trie.nodesSize());
    }

    @Test
    public void getUnknownValueWithEmptyKeyAsNull() {
        Trie trie = Trie.getEmptyTrie();

        Assert.assertNull(trie.get(new byte[0]));
        Assert.assertEquals(1, trie.nodesSize());
    }

    @Test
    public void putAndGetKeyValue() {
        byte[] key = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value = new byte[] { 0x01, 0x02, 0x03 };

        Trie trie = Trie.getEmptyTrie();
        trie = trie.put(key, value);

        Assert.assertNotNull(trie);
        Assert.assertArrayEquals(value, trie.get(key));
        Assert.assertEquals(5, trie.nodesSize());
    }

    @Test
    public void putAndGetValueUsingKeyWithLeftZeroBytes() {
        byte[] key = new byte[] { 0x00, 0x00, (byte)0xab, (byte)0xcd };
        byte[] key1 = new byte[] { 0x00, (byte)0xab, (byte)0xcd };
        byte[] key2 = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value = new byte[] { 0x01, 0x02, 0x03 };

        Trie trie = Trie.getEmptyTrie();
        trie = trie.put(key, value);

        Assert.assertNotNull(trie);
        Assert.assertArrayEquals(value, trie.get(key));
        Assert.assertArrayEquals(value, trie.get(key1));
        Assert.assertArrayEquals(value, trie.get(key2));
        Assert.assertEquals(5, trie.nodesSize());
    }


    @Test
    public void putAndGetValueUsingKeyWithLeftZeroNibbles() {
        byte[] key = new byte[] { 0x00, 0x00, 0x0f, (byte)0xab, (byte)0xcd };
        byte[] key1 = new byte[] { 0x00, 0x0f, (byte)0xab, (byte)0xcd };
        byte[] key2 = new byte[] { 0x0f, (byte)0xab, (byte)0xcd };
        byte[] value = new byte[] { 0x01, 0x02, 0x03 };

        Trie trie = Trie.getEmptyTrie();
        trie = trie.put(key, value);

        Assert.assertNotNull(trie);
        Assert.assertArrayEquals(value, trie.get(key));
        Assert.assertArrayEquals(value, trie.get(key1));
        Assert.assertArrayEquals(value, trie.get(key2));
        Assert.assertEquals(6, trie.nodesSize());
    }

    @Test
    public void putAndGetKeyNullValue() {
        byte[] key = new byte[0];
        byte[] value = new byte[] { 0x01, 0x02, 0x03 };

        Trie trie = Trie.getEmptyTrie();
        trie = trie.put(key, value);

        Assert.assertNotNull(trie);
        Assert.assertArrayEquals(value, trie.get(key));
        Assert.assertEquals(1, trie.nodesSize());
    }

    @Test
    public void putRemoveAndGetKeyValue() {
        byte[] key = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value = new byte[] { 0x01, 0x02, 0x03 };

        Trie trie = Trie.getEmptyTrie();
        trie = trie.put(key, value).delete(key);
        Assert.assertNotNull(trie);
        Assert.assertNull(trie.get(key));
        Assert.assertEquals(1, trie.nodesSize());
    }

    @Test
    public void putAndGetTwoKeyValues() {
        byte[] key1 = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value1 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] key2 = new byte[] { (byte)0xcd, (byte)0xab };
        byte[] value2 = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        byte[] key3 = new byte[] { (byte)0xcd, (byte)0xaa };

        Trie trie = Trie.getEmptyTrie().put(key1, value1).put(key2, value2);

        Assert.assertNotNull(trie);
        Assert.assertArrayEquals(value1, trie.get(key1));
        Assert.assertArrayEquals(value2, trie.get(key2));
        Assert.assertNull(trie.get(key3));
        Assert.assertEquals(9, trie.nodesSize());
    }

    @Test
    public void putAndGetTwoKeyValuesSamePath() {
        byte[] key1 = new byte[] { (byte)0xab };
        byte[] value1 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] key2 = new byte[] { (byte)0xab, (byte)0xab };
        byte[] value2 = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        byte[] key3 = new byte[] { (byte)0xcd, (byte)0xaa };

        Trie trie = Trie.getEmptyTrie().put(key1, value1).put(key2, value2);

        Assert.assertNotNull(trie);
        Assert.assertArrayEquals(value1, trie.get(key1));
        Assert.assertArrayEquals(value2, trie.get(key2));
        Assert.assertNull(trie.get(key3));
        Assert.assertEquals(5, trie.nodesSize());
    }

    @Test
    public void putChangeAndGetKeyValue() {
        byte[] key1 = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value1 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] value2 = new byte[] { 0x01, 0x02, 0x03, 0x04 };

        Trie trie = Trie.getEmptyTrie().put(key1, value1).put(key1, value2);

        Assert.assertNotNull(trie);
        Assert.assertArrayEquals(value2, trie.get(key1));
        Assert.assertEquals(5, trie.nodesSize());
    }
}
