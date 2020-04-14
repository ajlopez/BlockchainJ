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
    private static final MerkleTree[] EMPTY_NODE_ARRAY = new MerkleTree[0];

    private final Hash[] hashes;
    private final MerkleTree[] nodes;

    public static MerkleTree fromHashes(List<Hash> hashes) {
        return new MerkleTree(hashes.toArray(EMPTY_HASH_ARRAY));
    }

    public static MerkleTree fromNodes(List<MerkleTree> nodes) {
        return new MerkleTree(nodes.toArray(EMPTY_NODE_ARRAY));
    }

    public MerkleTree() {
        this.hashes = new Hash[0];
        this.nodes = null;
    }

    public MerkleTree(Hash[] hashes) {
        this.hashes = hashes;
        this.nodes = null;
    }

    public MerkleTree(MerkleTree[] nodes) {
        this.nodes = nodes;
        this.hashes = new Hash[this.nodes.length];
    }

    public boolean isLeaf() {
        return this.nodes == null;
    }

    public int size() {
        if (this.isLeaf())
            return this.hashes.length;

        int size = 0;

        for (int k = 0; k < this.nodes.length; k++)
            size += this.nodes[k].size();

        return size;
    }

    public int getDepth() {
        int depth = 1;

        if (!this.isLeaf())
            depth += this.nodes[0].getDepth();

        return depth;
    }

    public int getArity() {
        if (this.isLeaf())
            return this.hashes.length;

        return this.nodes[0].getArity();
    }

    public Hash getHash() {
        int nhashes = this.hashes.length;

        if (nhashes == 0)
            return EMPTY_MERKLE_TREE_HASH;

        if (this.hashes[0] == null)
            for (int k = 0; k < nhashes; k++)
                this.hashes[k] = this.nodes[k].getHash();

        byte[] bytes = new byte[Hash.HASH_BYTES * nhashes];

        for (int k = 0; k < nhashes; k++)
            System.arraycopy(this.hashes[k].getBytes(), 0, bytes, k * Hash.HASH_BYTES, Hash.HASH_BYTES);

        return HashUtils.calculateHash(bytes);
    }

    public Hash[] getLeftHashes(int position) {
        Hash[] leftHashes = new Hash[position];

        System.arraycopy(this.hashes, 0, leftHashes, 0, leftHashes.length);

        return leftHashes;
    }

    public Hash[] getRightHashes(int position) {
        Hash[] rightHashes = new Hash[this.hashes.length - position - 1];

        System.arraycopy(this.hashes, position + 1, rightHashes, 0, rightHashes.length);

        return rightHashes;
    }

    public MerkleTree getNode(int position) {
        return this.nodes[position];
    }
}
