package com.ajlopez.blockchain.merkle;

import com.ajlopez.blockchain.core.types.Hash;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HashUtils;

import java.util.List;

/**
 * Created by ajlopez on 05/04/2020.
 */
public class MerkleTree {
    public static final Hash EMPTY_MERKLE_TREE_HASH = HashUtils.calculateHash(ByteUtils.EMPTY_BYTE_ARRAY);
    private static final Hash[] EMPTY_HASH_ARRAY = new Hash[0];

    private final Hash[] hashes;

    public MerkleTree() {
        this.hashes = new Hash[0];
    }

    public MerkleTree(List<Hash> hashes) {
        this.hashes = hashes.toArray(EMPTY_HASH_ARRAY);
    }

    public boolean isLeaf() {
        return true;
    }

    public Hash getHash() {
        int nhashes = this.hashes.length;

        if (nhashes == 0)
            return EMPTY_MERKLE_TREE_HASH;

        byte[] bytes = new byte[Hash.HASH_BYTES * nhashes];

        for (int k = 0; k < nhashes; k++)
            System.arraycopy(this.hashes[k].getBytes(), 0, bytes, k * Hash.HASH_BYTES, Hash.HASH_BYTES);

        return HashUtils.calculateHash(bytes);
    }
}
