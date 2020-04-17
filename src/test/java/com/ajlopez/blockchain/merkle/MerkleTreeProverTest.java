package com.ajlopez.blockchain.merkle;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by ajlopez on 15/04/2020.
 */
public class MerkleTreeProverTest {
    @Test
    public void emptyMerkleTreeProof() {
        MerkleTree merkleTree = new MerkleTreeBuilder().build();
        MerkleTreeProver merkleTreeProver = new MerkleTreeProver(merkleTree);

        List<Pair<Hash[], Hash[]>> proof = merkleTreeProver.getProof(0);

        Assert.assertNotNull(proof);
        Assert.assertTrue(proof.isEmpty());
    }

    @Test
    public void merkleTreeWithOneHashProof() {
        Hash hash = FactoryHelper.createRandomHash();
        MerkleTree merkleTree = new MerkleTreeBuilder()
                .add(hash)
                .build();
        MerkleTreeProver merkleTreeProver = new MerkleTreeProver(merkleTree);

        List<Pair<Hash[], Hash[]>> proof = merkleTreeProver.getProof(0);

        Assert.assertNotNull(proof);
        Assert.assertFalse(proof.isEmpty());
        Assert.assertEquals(1, proof.size());
        Assert.assertEquals(0, proof.get(0).getKey().length);
        Assert.assertEquals(0, proof.get(0).getValue().length);
    }

    @Test
    public void merkleTreeWithTwoHashesProof() {
        Hash hash1 = FactoryHelper.createRandomHash();
        Hash hash2 = FactoryHelper.createRandomHash();

        MerkleTree merkleTree = new MerkleTreeBuilder()
                .add(hash1)
                .add(hash2)
                .build();

        MerkleTreeProver merkleTreeProver = new MerkleTreeProver(merkleTree);

        List<Pair<Hash[], Hash[]>> proof1 = merkleTreeProver.getProof(0);

        Assert.assertNotNull(proof1);
        Assert.assertFalse(proof1.isEmpty());
        Assert.assertEquals(1, proof1.size());
        Assert.assertEquals(0, proof1.get(0).getKey().length);
        Assert.assertEquals(1, proof1.get(0).getValue().length);
        Assert.assertEquals(hash2, proof1.get(0).getValue()[0]);

        List<Pair<Hash[], Hash[]>> proof2 = merkleTreeProver.getProof(1);

        Assert.assertNotNull(proof2);
        Assert.assertFalse(proof2.isEmpty());
        Assert.assertEquals(1, proof2.size());
        Assert.assertEquals(1, proof2.get(0).getKey().length);
        Assert.assertEquals(0, proof2.get(0).getValue().length);
        Assert.assertEquals(hash1, proof2.get(0).getKey()[0]);
    }

    @Test
    public void merkleTreeWithThreeHashesProof() {
        Hash hash1 = FactoryHelper.createRandomHash();
        Hash hash2 = FactoryHelper.createRandomHash();
        Hash hash3 = FactoryHelper.createRandomHash();

        MerkleTree merkleTree = new MerkleTreeBuilder()
                .add(hash1)
                .add(hash2)
                .add(hash3)
                .build();

        MerkleTreeProver merkleTreeProver = new MerkleTreeProver(merkleTree);

        List<Pair<Hash[], Hash[]>> proof1 = merkleTreeProver.getProof(0);

        Assert.assertNotNull(proof1);
        Assert.assertFalse(proof1.isEmpty());
        Assert.assertEquals(2, proof1.size());
        Assert.assertEquals(0, proof1.get(0).getKey().length);
        Assert.assertEquals(1, proof1.get(0).getValue().length);
        Assert.assertEquals(0, proof1.get(1).getKey().length);
        Assert.assertEquals(1, proof1.get(1).getValue().length);
        Assert.assertEquals(hash2, proof1.get(1).getValue()[0]);

        List<Pair<Hash[], Hash[]>> proof2 = merkleTreeProver.getProof(1);

        Assert.assertNotNull(proof2);
        Assert.assertFalse(proof2.isEmpty());
        Assert.assertEquals(2, proof2.size());
        Assert.assertEquals(0, proof2.get(0).getKey().length);
        Assert.assertEquals(1, proof2.get(0).getValue().length);
        Assert.assertEquals(1, proof2.get(1).getKey().length);
        Assert.assertEquals(0, proof2.get(1).getValue().length);
        Assert.assertEquals(hash1, proof2.get(1).getKey()[0]);

        List<Pair<Hash[], Hash[]>> proof3 = merkleTreeProver.getProof(2);

        Assert.assertNotNull(proof3);
        Assert.assertFalse(proof3.isEmpty());
        Assert.assertEquals(2, proof3.size());
        Assert.assertEquals(1, proof3.get(0).getKey().length);
        Assert.assertEquals(0, proof3.get(0).getValue().length);
        Assert.assertEquals(0, proof3.get(1).getKey().length);
        Assert.assertEquals(0, proof3.get(1).getValue().length);
    }
}
