package com.ajlopez.blockchain.merkle;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HashUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
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
        Assert.assertEquals(0, merkleTree.size());
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

        MerkleTree merkleTree = MerkleTree.fromHashes(hashes);

        Assert.assertTrue(merkleTree.isLeaf());

        Hash result = merkleTree.getHash();

        Assert.assertNotNull(result);
        Assert.assertNotEquals(MerkleTree.EMPTY_MERKLE_TREE_HASH, result);

        Hash expected = HashUtils.calculateHash(hash.getBytes());

        Assert.assertEquals(expected, result);

        Assert.assertEquals(1, merkleTree.size());
    }

    @Test
    public void createMerkleTreeWithTwoHashes() {
        Hash hash1 = FactoryHelper.createRandomHash();
        Hash hash2 = FactoryHelper.createRandomHash();
        List<Hash> hashes = new ArrayList<>();
        hashes.add(hash1);
        hashes.add(hash2);

        MerkleTree merkleTree = MerkleTree.fromHashes(hashes);

        Assert.assertTrue(merkleTree.isLeaf());

        Hash result = merkleTree.getHash();

        Assert.assertNotNull(result);
        Assert.assertNotEquals(MerkleTree.EMPTY_MERKLE_TREE_HASH, result);

        Hash expected = HashUtils.calculateHash(ByteUtils.concatenate(hash1.getBytes(), hash2.getBytes()));

        Assert.assertEquals(expected, result);

        Assert.assertEquals(2, merkleTree.size());
    }

    @Test
    public void createMerkleTreeWithTwoNodes() {
        Hash hash1 = FactoryHelper.createRandomHash();
        Hash hash2 = FactoryHelper.createRandomHash();
        List<Hash> hashes1 = Collections.singletonList(hash1);
        List<Hash> hashes2 = Collections.singletonList(hash2);

        MerkleTree node1 = MerkleTree.fromHashes(hashes1);
        MerkleTree node2 = MerkleTree.fromHashes(hashes2);

        List<MerkleTree> nodes = new ArrayList<>();
        nodes.add(node1);
        nodes.add(node2);

        MerkleTree merkleTree = MerkleTree.fromNodes(nodes);

        Assert.assertFalse(merkleTree.isLeaf());

        Hash result = merkleTree.getHash();

        Assert.assertNotNull(result);
        Assert.assertNotEquals(MerkleTree.EMPTY_MERKLE_TREE_HASH, result);

        Hash expected = HashUtils.calculateHash(ByteUtils.concatenate(node1.getHash().getBytes(), node2.getHash().getBytes()));

        Assert.assertEquals(expected, result);

        Assert.assertSame(node1, merkleTree.getNode(0));
        Assert.assertSame(node2, merkleTree.getNode(1));

        Assert.assertEquals(2, merkleTree.size());
    }

    @Test
    public void getLeftAndRightHashes() {
        Hash[] hashes = new Hash[16];

        for (int k = 0; k < hashes.length; k++)
            hashes[k] = FactoryHelper.createRandomHash();

        MerkleTree merkleTree = MerkleTree.fromHashes(Arrays.asList(hashes));

        Assert.assertEquals(hashes.length, merkleTree.size());

        for (int k = 0; k < hashes.length; k++) {
            Hash[] leftHashes = merkleTree.getLeftHashes(k);
            Hash[] rightHashes = merkleTree.getRightHashes(k);

            Assert.assertNotNull(leftHashes);
            Assert.assertNotNull(rightHashes);

            Assert.assertEquals(k, leftHashes.length);
            Assert.assertEquals(15 - k, rightHashes.length);

            for (int j = 0; j < leftHashes.length; j++)
                Assert.assertEquals(hashes[j], leftHashes[j]);

            for (int j = 0; j < rightHashes.length; j++)
                Assert.assertEquals(hashes[k + 1 + j], rightHashes[j]);
        }
    }
}
