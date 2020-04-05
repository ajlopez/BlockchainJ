package com.ajlopez.blockchain.merkle;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HashUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 05/04/2020.
 */
public class MerkleTreeTest {
    @Test
    public void createLeafNode() {
        MerkleTree merkleTree = new MerkleTree();

        Assert.assertTrue(merkleTree.isLeaf());
    }

    @Test
    public void getHashFromEmptyTree() {
        MerkleTree merkleTree = new MerkleTree();

        Hash result = merkleTree.getHash();

        Assert.assertNotNull(result);
        Assert.assertEquals(MerkleTree.EMPTY_MERKLE_TREE_HASH, result);
    }

    @Test
    public void createMerkleTreeWithOneHash() {
        Hash hash = FactoryHelper.createRandomHash();
        List<Hash> hashes = Collections.singletonList(hash);

        MerkleTree merkleTree = new MerkleTree(hashes);

        Assert.assertTrue(merkleTree.isLeaf());

        Hash result = merkleTree.getHash();

        Assert.assertNotNull(result);
        Assert.assertNotEquals(MerkleTree.EMPTY_MERKLE_TREE_HASH, result);

        Hash expected = HashUtils.calculateHash(hash.getBytes());

        Assert.assertEquals(expected, result);
    }

    @Test
    public void createMerkleTreeWithTwoHashes() {
        Hash hash1 = FactoryHelper.createRandomHash();
        Hash hash2 = FactoryHelper.createRandomHash();
        List<Hash> hashes = new ArrayList<>();
        hashes.add(hash1);
        hashes.add(hash2);

        MerkleTree merkleTree = new MerkleTree(hashes);

        Assert.assertTrue(merkleTree.isLeaf());

        Hash result = merkleTree.getHash();

        Assert.assertNotNull(result);
        Assert.assertNotEquals(MerkleTree.EMPTY_MERKLE_TREE_HASH, result);

        Hash expected = HashUtils.calculateHash(ByteUtils.concatenate(hash1.getBytes(), hash2.getBytes()));

        Assert.assertEquals(expected, result);
    }
}
