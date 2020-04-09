package com.ajlopez.blockchain.merkle;

import com.ajlopez.blockchain.core.types.Hash;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 06/04/2020.
 */
public class MerkleTreeBuilder {
    private int arity = 2;
    private final List<Hash> hashes = new ArrayList<>();

    public int getArity() { return this.arity; }

    public MerkleTreeBuilder add(Hash hash) {
        this.hashes.add(hash);

        return this;
    }

    public MerkleTreeBuilder arity(int newarity) {
        this.arity = newarity;

        return this;
    }

    public MerkleTree build() {
        if (this.hashes.isEmpty())
            return new MerkleTree();

        List<Hash> nodeHashes = new ArrayList<>();
        List<MerkleTree> nodes = new ArrayList<>();

        for (Hash hash: this.hashes) {
            nodeHashes.add(hash);

            if (nodeHashes.size() == arity) {
                nodes.add(MerkleTree.fromHashes(nodeHashes));
                nodeHashes.clear();
            }
        }

        if (!nodeHashes.isEmpty())
            nodes.add(MerkleTree.fromHashes(nodeHashes));

        while (nodes.size() > 1) {
            List<MerkleTree> newNodes = new ArrayList<>();
            List<MerkleTree> subnodes = new ArrayList<>();

            for (MerkleTree node : nodes) {
                subnodes.add(node);

                if (subnodes.size() == arity) {
                    newNodes.add(MerkleTree.fromNodes(subnodes));
                    subnodes.clear();
                }
            }

            if (!subnodes.isEmpty())
                newNodes.add(MerkleTree.fromNodes(subnodes));

            nodes = newNodes;
        }

        return nodes.get(0);
    }
}
