package com.ajlopez.blockchain.state;

import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.TrieStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.HashUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by ajlopez on 12/08/2017.
 */
public class TrieTest {
    private static Random random = new Random();

    @Test
    public void getUnknownValueAsNull() throws IOException {
        Trie trie = new Trie();

        Assert.assertNull(trie.get(new byte[] { 0x01, 0x02 }));
        Assert.assertEquals(1, trie.nodesSize());
        Assert.assertNull(trie.getValue());
    }

    @Test
    public void getUnknownValuePathAsEmpty() throws IOException {
        Trie trie = new Trie();

        TriePath result = trie.getPath(new byte[] { 0x01, 0x02 });

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.size());
    }

    @Test
    public void getEncodedEmptyTrie() {
        Trie trie = new Trie();

        byte[] encoded = trie.getEncoded();

        Assert.assertNotNull(encoded);
        Assert.assertEquals(6, encoded.length);

        byte[] expected = new byte[6];
        expected[1] = 16; // arity

        Assert.assertArrayEquals(expected, encoded);
    }

    @Test
    public void getSubhashesFromEmptyTrie() {
        Trie trie = new Trie();

        Hash[] hashes = trie.getSubHashes();

        Assert.assertNotNull(hashes);
        Assert.assertEquals(Trie.ARITY, hashes.length);

        for (int k = 0; k < Trie.ARITY; k++)
            Assert.assertNull(hashes[k]);
    }

    @Test
    public void getSubhashesFromTrieWithOneKeyValue() throws IOException {
        Trie trie = new Trie().put(FactoryHelper.createRandomBytes(32), FactoryHelper.createRandomBytes(42));

        Hash[] hashes = trie.getSubHashes();

        Assert.assertNotNull(hashes);
        Assert.assertEquals(Trie.ARITY, hashes.length);

        int nhashes = 0;

        for (int k = 0; k < Trie.ARITY; k++)
            if (hashes[k] != null)
                nhashes++;

        Assert.assertEquals(0, nhashes);
    }

    @Test
    public void getPathFromTrieWithOneKeyValue() throws IOException {
        byte[] key = FactoryHelper.createRandomBytes(32);
        byte[] value = FactoryHelper.createRandomBytes(32);

        Trie trie = new Trie().put(key, value);

        TriePath triePath = trie.getPath(key);

        Assert.assertNotNull(triePath);
        Assert.assertNotEquals(0, triePath.size());
        Assert.assertEquals(1, triePath.size());
        Assert.assertArrayEquals(value, triePath.getTrie(0).getValue());
    }

    @Test
    public void getPathFromTrieWithTwoKeyValues() throws IOException {
        byte[] key = FactoryHelper.createRandomBytes(32);
        byte[] value = FactoryHelper.createRandomBytes(32);
        byte[] key2 = FactoryHelper.createRandomBytes(32);
        byte[] value2 = FactoryHelper.createRandomBytes(32);

        Trie trie = new Trie().put(key, value).put(key2, value2);

        TriePath triePath = trie.getPath(key);

        Assert.assertNotNull(triePath);
        Assert.assertNotEquals(0, triePath.size());
        Assert.assertEquals(2, triePath.size());
        Assert.assertArrayEquals(value, triePath.getTrie(1).getValue());
    }

    @Test
    public void getSubhashesFromTrieIsACopy() throws IOException {
        Trie trie = new Trie().put(FactoryHelper.createRandomBytes(32), FactoryHelper.createRandomBytes(42));

        Hash[] hashes = trie.getSubHashes();

        Assert.assertNotNull(hashes);
        Assert.assertEquals(Trie.ARITY, hashes.length);

        Hash[] copy = new Hash[Trie.ARITY];

        System.arraycopy(hashes, 0, copy, 0, Trie.ARITY);

        for (int k = 0; k < Trie.ARITY; k++)
            if (hashes[k] != null)
                hashes[k] = FactoryHelper.createRandomHash();

        Hash[] newhashes = trie.getSubHashes();

        Assert.assertNotNull(newhashes);
        Assert.assertEquals(Trie.ARITY, newhashes.length);
        Assert.assertArrayEquals(copy, newhashes);
    }

    @Test
    public void getEncodedTrieWithValueAndNoSubNodes() throws IOException {
        byte[] value = new byte[32];
        random.nextBytes(value);

        Trie trie = new Trie().put(new byte[0], value);

        byte[] encoded = trie.getEncoded();

        Assert.assertNotNull(encoded);
        Assert.assertEquals(6 + Integer.BYTES + 32, encoded.length);

        byte[] expected = new byte[6 + Integer.BYTES + 32];
        expected[1] = 16; // arity
        expected[2] = Integer.BYTES; // value length in bytes
        expected[9] = 32; // value length in bytes[5..8]
        System.arraycopy(value, 0, expected, 10, value.length);

        Assert.assertArrayEquals(expected, encoded);
    }

    @Test
    public void getEncodedTrieWithoutValueAndTwoSubNodes() throws IOException {
        byte[] value = new byte[32];
        random.nextBytes(value);

        Trie trie = new Trie().put(new byte[] { 0x01 }, value).put(new byte[] { 0x02 }, value);

        Assert.assertNull(trie.getValue());

        byte[] encoded = trie.getEncoded();

        Assert.assertNotNull(encoded);
        Assert.assertEquals(4 + Short.BYTES + HashUtils.HASH_BYTES * 2 + Short.BYTES + 1, encoded.length);

        byte[] firstexpected = new byte[6];
        firstexpected[1] = 16; // arity
        firstexpected[3] = 2; // shared key length bytes
        firstexpected[5] = 2 | 4; // two subnodes

        Assert.assertArrayEquals(firstexpected, Arrays.copyOfRange(encoded, 0, 6));
    }

    @Test
    public void retrieveTrieFromEncodedEmptyTrie() {
        Trie trie = new Trie();

        byte[] encoded = trie.getEncoded();

        Trie result = Trie.fromEncoded(encoded, null);

        Assert.assertNotNull(result);
        Assert.assertEquals(trie.getHash(), result.getHash());
    }

    @Test
    public void retrieveFromEncodedTrieWithKeyValueInSubTrie() throws IOException {
        byte[] value = new byte[32];
        random.nextBytes(value);
        byte[] key = new byte[] { 0x01 };
        Trie trie = new Trie().put(key, value);

        byte[] encoded = trie.getEncoded();

        Trie result = Trie.fromEncoded(encoded, null);

        Assert.assertNotNull(result);
        Assert.assertEquals(trie.getHash(), result.getHash());
    }

    @Test
    public void putKeyValueTwice() throws IOException {
        byte[] value1 = new byte[Hash.HASH_BYTES];
        random.nextBytes(value1);
        byte[] value2 = new byte[Hash.HASH_BYTES];
        random.nextBytes(value2);

        byte[] key = new byte[Address.ADDRESS_BYTES];
        random.nextBytes(key);

        Trie trie1 = new Trie().put(key, value1);
        Trie trie2 = trie1.put(key, value2);

        Assert.assertArrayEquals(value1, trie1.get(key));
        Assert.assertArrayEquals(value2, trie2.get(key));
        Assert.assertNotEquals(trie1.getHash(), trie2.getHash());
    }

    @Test
    public void putKeyValueTwiceUsingSave() throws IOException {
        byte[] value1 = new byte[Hash.HASH_BYTES];
        random.nextBytes(value1);
        byte[] value2 = new byte[Hash.HASH_BYTES];
        random.nextBytes(value2);

        byte[] key = new byte[Address.ADDRESS_BYTES];
        random.nextBytes(key);

        Trie trie1 = new Trie(new TrieStore(new HashMapStore())).put(key, value1);
        trie1.save();

        Trie trie2 = trie1.put(key, value2);
        trie2.save();

        Assert.assertArrayEquals(value1, trie1.get(key));
        Assert.assertArrayEquals(value2, trie2.get(key));
        Assert.assertNotEquals(trie1.getHash(), trie2.getHash());
    }

    @Test
    public void putKeySameValueTwiceUsingSaveAndStore() throws IOException {
        byte[] value = FactoryHelper.createRandomBytes(Hash.HASH_BYTES);
        byte[] key = FactoryHelper.createRandomBytes(Address.ADDRESS_BYTES);

        random.nextBytes(key);

        TrieStore store = new TrieStore(new HashMapStore());
        Trie trie1 = new Trie(store).put(key, value);
        trie1.save();

        Trie trie1b = store.retrieve(trie1.getHash());

        TriePath path1 = trie1b.getPath(key);
        Trie trie2 = trie1b.put(key, value);
        TriePath path2 = trie1b.getPath(key);

        Assert.assertArrayEquals(value, trie1.get(key));
        Assert.assertArrayEquals(value, trie2.get(key));
        Assert.assertEquals(trie1.getHash(), trie2.getHash());

        Assert.assertEquals(path1.size(), path2.size());

        for (int k = 0; k < path1.size(); k++)
            Assert.assertSame(path1.getTrie(k), path2.getTrie(k));
    }

    @Test
    public void retrieveTrieFromEncodedTrieWithValue() throws IOException {
        byte[] value = new byte[32];
        random.nextBytes(value);
        byte[] key = new byte[0];
        Trie trie = new Trie().put(key, value);

        byte[] encoded = trie.getEncoded();

        Trie result = Trie.fromEncoded(encoded, null);

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(encoded, result.getEncoded());
        Assert.assertEquals(trie.getHash(), result.getHash());
        Assert.assertArrayEquals(value, trie.get(key));
    }

    @Test
    public void getHashFromEmptyTrie() {
        Trie trie = new Trie();

        Hash hash = trie.getHash();

        Assert.assertNotNull(hash);
    }

    @Test
    public void getHashFromTrieWithValueAndNoSubNodes() throws IOException {
        byte[] value = new byte[32];
        random.nextBytes(value);

        Trie trie = new Trie().put(new byte[0], value);

        Hash hash = trie.getHash();

        Assert.assertNotNull(hash);

        Trie trie2 = new Trie().put(new byte[0], value);

        Hash hash2 = trie2.getHash();

        Assert.assertEquals(hash, hash2);
    }

    @Test
    public void getUnknownValueWithEmptyKeyAsNull() throws IOException {
        Trie trie = new Trie();

        Assert.assertNull(trie.get(new byte[0]));
        Assert.assertEquals(1, trie.nodesSize());
    }

    @Test
    public void putAndGetKeyValue() throws IOException {
        byte[] key = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value = new byte[] { 0x01, 0x02, 0x03 };

        Trie trie = new Trie();
        trie = trie.put(key, value);

        Assert.assertNotNull(trie);
        Assert.assertArrayEquals(value, trie.get(key));
        Assert.assertEquals(1, trie.nodesSize());
    }

    @Test
    public void putAndGetTwoKeyValue() throws IOException {
        byte[] key1 = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value1 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] key2 = new byte[] { (byte)0xab, (byte)0xce };
        byte[] value2 = new byte[] { 0x01, 0x02, 0x03, 0x04 };

        Trie trie = new Trie();
        trie = trie.put(key1, value1);
        trie = trie.put(key2, value2);

        Assert.assertNotNull(trie);
        Assert.assertArrayEquals(value1, trie.get(key1));
        Assert.assertArrayEquals(value2, trie.get(key2));

        Assert.assertEquals(3, trie.nodesSize());
    }

    @Test
    public void putAndGetKeyNullValue() throws IOException {
        byte[] key = new byte[0];
        byte[] value = new byte[] { 0x01, 0x02, 0x03 };

        Trie trie = new Trie();
        trie = trie.put(key, value);

        Assert.assertNotNull(trie);
        Assert.assertArrayEquals(value, trie.get(key));
        Assert.assertEquals(1, trie.nodesSize());
    }

    @Test
    public void putRemoveAndGetKeyValue() throws IOException {
        byte[] key = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value = new byte[] { 0x01, 0x02, 0x03 };

        Trie trie = new Trie();
        trie = trie.put(key, value).delete(key);
        Assert.assertNotNull(trie);
        Assert.assertNull(trie.get(key));
        Assert.assertEquals(1, trie.nodesSize());
        Assert.assertEquals((new Trie()).getHash(), trie.getHash());
    }

    @Test
    public void putKeyValueAndRemoveUnknownKey() throws IOException {
        byte[] key1 = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value1 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] key2 = new byte[] { (byte)0xab, (byte)0xce };

        Trie trie = new Trie();
        trie = trie.put(key1, value1);

        Trie trie2 = trie.delete(key2);

        Assert.assertSame(trie, trie2);
    }

    @Test
    public void putTwoKeyValuesAndRemoveUnknownKey() throws IOException {
        byte[] key1 = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value1 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] key2 = new byte[] { (byte)0xab, (byte)0xce };
        byte[] value2 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] key3 = new byte[] { (byte)0xab, (byte)0xcf };

        Trie trie = new Trie();
        trie = trie.put(key1, value1);
        trie = trie.put(key2, value2);

        Trie trie2 = trie.delete(key3);

        Assert.assertSame(trie, trie2);
    }

    @Test
    public void putRemoveAndGetTwoKeyValue() throws IOException {
        byte[] key1 = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value1 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] key2 = new byte[] { (byte)0xab, (byte)0xce };
        byte[] value2 = new byte[] { 0x01, 0x02, 0x03, 0x04 };

        Trie trie = new Trie();
        trie = trie.put(key1, value1)
                .put(key2, value2)
                .delete(key1)
                .delete(key2);

        Assert.assertNotNull(trie);
        Assert.assertNull(trie.get(key1));
        Assert.assertNull(trie.get(key2));
        Assert.assertEquals(1, trie.nodesSize());
        Assert.assertEquals((new Trie()).getHash(), trie.getHash());
    }

    @Test
    public void putRemoveAndGetTwoKeyValueWithShortSharedKey() throws IOException {
        byte[] key1 = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value1 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] key2 = new byte[] { (byte)0xac, (byte)0xcd };
        byte[] value2 = new byte[] { 0x01, 0x02, 0x03, 0x04 };

        Trie trie = new Trie();
        trie = trie.put(key1, value1)
                .put(key2, value2)
                .delete(key1)
                .delete(key2);

        Assert.assertNotNull(trie);
        Assert.assertNull(trie.get(key1));
        Assert.assertNull(trie.get(key2));
        Assert.assertEquals(1, trie.nodesSize());
        Assert.assertEquals((new Trie()).getHash(), trie.getHash());
    }

    @Test
    public void putTwoKeyValuesRemoveOneKeyValue() throws IOException {
        byte[] key1 = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value1 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] key2 = new byte[] { (byte)0xac, (byte)0xcd };
        byte[] value2 = new byte[] { 0x01, 0x02, 0x03, 0x04 };

        Trie trie = new Trie();
        trie = trie.put(key1, value1)
                .put(key2, value2)
                .delete(key1);

        Trie trie2 = new Trie().put(key2, value2);

        Assert.assertNotNull(trie);
        Assert.assertNull(trie.get(key1));
        Assert.assertArrayEquals(value2, trie.get(key2));

        Assert.assertNotNull(trie2);
        Assert.assertNull(trie2.get(key1));
        Assert.assertArrayEquals(value2, trie2.get(key2));

        Assert.assertEquals(trie.getHash(), trie2.getHash());
    }

    @Test
    public void putAndGetTwoKeyValues() throws IOException {
        byte[] key1 = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value1 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] key2 = new byte[] { (byte)0xcd, (byte)0xab };
        byte[] value2 = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        byte[] key3 = new byte[] { (byte)0xcd, (byte)0xaa };

        Trie trie = new Trie().put(key1, value1).put(key2, value2);

        Assert.assertNotNull(trie);
        Assert.assertArrayEquals(value1, trie.get(key1));
        Assert.assertArrayEquals(value2, trie.get(key2));
        Assert.assertNull(trie.get(key3));
        Assert.assertEquals(3, trie.nodesSize());
    }

    @Test
    public void putAndGetTwoKeyValuesSamePath() throws IOException {
        byte[] key1 = new byte[] { (byte)0xab };
        byte[] value1 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] key2 = new byte[] { (byte)0xab, (byte)0xab };
        byte[] value2 = new byte[] { 0x01, 0x02, 0x03, 0x04 };
        byte[] key3 = new byte[] { (byte)0xcd, (byte)0xaa };

        Trie trie = new Trie().put(key1, value1).put(key2, value2);

        Assert.assertNotNull(trie);
        Assert.assertArrayEquals(value1, trie.get(key1));
        Assert.assertArrayEquals(value2, trie.get(key2));
        Assert.assertNull(trie.get(key3));
        Assert.assertEquals(2, trie.nodesSize());
    }

    @Test
    public void putChangeAndGetKeyValue() throws IOException {
        byte[] key1 = new byte[] { (byte)0xab, (byte)0xcd };
        byte[] value1 = new byte[] { 0x01, 0x02, 0x03 };
        byte[] value2 = new byte[] { 0x01, 0x02, 0x03, 0x04 };

        Trie trie = new Trie().put(key1, value1).put(key1, value2);

        Assert.assertNotNull(trie);
        Assert.assertArrayEquals(value2, trie.get(key1));
        Assert.assertEquals(1, trie.nodesSize());
    }
}
