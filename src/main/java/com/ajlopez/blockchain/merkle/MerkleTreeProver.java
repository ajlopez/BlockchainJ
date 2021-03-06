package com.ajlopez.blockchain.merkle;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.utils.HashUtils;
import javafx.util.Pair;

import java.util.ArrayList;
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

        List<Pair<Hash[], Hash[]>> list = new ArrayList<>();

        MerkleTree mt = this.merkleTree;
        int p = position;
        int d = this.depth - 1;

        while (true) {
            int divisor = 1;

            for (int k = 0; k < d; k++)
                divisor *= this.arity;

            int nnode = p / divisor;

            list.add(new Pair<>(mt.getLeftHashes(nnode), mt.getRightHashes(nnode)));

            if (d == 0)
                break;

            d--;
            mt = mt.getNode(nnode);
            p = p - nnode * divisor;
        }

        return list;
    }

    public static Hash calculateHash(Hash seed, List<Pair<Hash[], Hash[]>> proof) {
        Hash hash = seed;
        int nlevels = proof.size();

        for (int k = 0; k < nlevels; k++) {
            Pair<Hash[], Hash[]> level = proof.get(nlevels - k - 1);
            Hash[] leftHashes = level.getKey();
            Hash[] rightHashes = level.getValue();

            byte[] hashBytes = new byte[(leftHashes.length + 1 + rightHashes.length) * Hash.HASH_BYTES];

            for (int j = 0; j < leftHashes.length; j++)
                System.arraycopy(leftHashes[j].getBytes(), 0, hashBytes, j * Hash.HASH_BYTES, Hash.HASH_BYTES);

            System.arraycopy(hash.getBytes(), 0, hashBytes, leftHashes.length * Hash.HASH_BYTES, Hash.HASH_BYTES);

            for (int j = 0; j < rightHashes.length; j++)
                System.arraycopy(rightHashes[j].getBytes(), 0, hashBytes, (leftHashes.length + 1 + j) * Hash.HASH_BYTES, Hash.HASH_BYTES);

            hash = HashUtils.calculateHash(hashBytes);
        }

        return hash;
    }
}
