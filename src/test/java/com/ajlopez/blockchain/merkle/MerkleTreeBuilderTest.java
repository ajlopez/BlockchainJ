package com.ajlopez.blockchain.merkle;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HashUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Angel on 06/04/2020.
 */
public class MerkleTreeBuilderTest {
    @Test
    public void defaultArity() {
        MerkleTreeBuilder merkleTreeBuilder = new MerkleTreeBuilder();

        Assert.assertEquals(2, merkleTreeBuilder.getArity());
    }

    @Test
    public void changeArityUsingFluentMethod() {
        MerkleTreeBuilder merkleTreeBuilder = new MerkleTreeBuilder();

        merkleTreeBuilder = merkleTreeBuilder.arity(16);

        Assert.assertNotNull(merkleTreeBuilder);
        Assert.assertEquals(16, merkleTreeBuilder.getArity());
    }

    @Test
    public void buildEmptyMerkleTree() {
        MerkleTree merkleTree = new MerkleTreeBuilder().build();

        Assert.assertNotNull(merkleTree);
        Assert.assertTrue(merkleTree.isLeaf());
        Assert.assertEquals(0, merkleTree.size());
        Assert.assertEquals(1, merkleTree.getDepth());
        Assert.assertEquals(MerkleTree.EMPTY_MERKLE_TREE_HASH, merkleTree.getHash());
    }

    @Test
    public void buildMerkleTreeWithOneHash() {
        Hash hash = FactoryHelper.createRandomHash();

        MerkleTree merkleTree = new MerkleTreeBuilder()
                .add(hash)
                .build();

        Assert.assertNotNull(merkleTree);
        Assert.assertTrue(merkleTree.isLeaf());
        Assert.assertEquals(1, merkleTree.size());
        Assert.assertEquals(1, merkleTree.getDepth());

        Hash expected = HashUtils.calculateHash(hash.getBytes());

        Assert.assertEquals(expected, merkleTree.getHash());
    }

    @Test
    public void buildMerkleTreeWithTwoHashes() {
        Hash hash1 = FactoryHelper.createRandomHash();
        Hash hash2 = FactoryHelper.createRandomHash();

        MerkleTree merkleTree = new MerkleTreeBuilder()
                .add(hash1)
                .add(hash2)
                .build();

        Assert.assertNotNull(merkleTree);
        Assert.assertTrue(merkleTree.isLeaf());
        Assert.assertEquals(2, merkleTree.size());
        Assert.assertEquals(1, merkleTree.getDepth());

        Hash expected = HashUtils.calculateHash(ByteUtils.concatenate(hash1.getBytes(), hash2.getBytes()));

        Assert.assertEquals(expected, merkleTree.getHash());
    }

    @Test
    public void buildMerkleTreeWithThreeHashes() {
        Hash hash1 = FactoryHelper.createRandomHash();
        Hash hash2 = FactoryHelper.createRandomHash();
        Hash hash3 = FactoryHelper.createRandomHash();

        MerkleTree merkleTree = new MerkleTreeBuilder()
                .add(hash1)
                .add(hash2)
                .add(hash3)
                .build();

        Assert.assertNotNull(merkleTree);
        Assert.assertFalse(merkleTree.isLeaf());
        Assert.assertEquals(3, merkleTree.size());
        Assert.assertEquals(2, merkleTree.getDepth());

        Hash expected = HashUtils.calculateHash(ByteUtils.concatenate(HashUtils.calculateHash(ByteUtils.concatenate(hash1.getBytes(), hash2.getBytes())).getBytes(), HashUtils.calculateHash(hash3.getBytes()).getBytes()));

        Assert.assertEquals(expected, merkleTree.getHash());
    }
}
