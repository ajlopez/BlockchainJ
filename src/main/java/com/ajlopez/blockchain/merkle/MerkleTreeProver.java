package com.ajlopez.blockchain.merkle;

import com.ajlopez.blockchain.core.types.Hash;
import javafx.util.Pair;

import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 14/04/2020.
 */
public class MerkleTreeProver {
    private final MerkleTree merkleTree;
    private final int size;
    private final int arity;
    private final int depth;

    public MerkleTreeProver(MerkleTree merkleTree) {
        this.merkleTree = merkleTree;
        this.size = merkleTree.size();
        this.arity = merkleTree.getArity();
        this.depth = merkleTree.getDepth();
    }

    public List<Pair<Hash[], Hash[]>> getProof(int position) {
        if (this.size == 0)
            return Collections.emptyList();

        if (this.size == 1)
            return Collections.singletonList(new Pair<>(this.merkleTree.getLeftHashes(0), this.merkleTree.getRightHashes(0)));

        return Collections.singletonList(new Pair<>(this.merkleTree.getLeftHashes(position), this.merkleTree.getRightHashes(position)));
    }
}
