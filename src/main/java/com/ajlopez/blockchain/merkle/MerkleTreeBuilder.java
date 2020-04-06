package com.ajlopez.blockchain.merkle;

/**
 * Created by ajlopez on 06/04/2020.
 */
public class MerkleTreeBuilder {
    private int arity = 2;

    public int getArity() { return this.arity; }

    public MerkleTreeBuilder arity(int newarity) {
        this.arity = newarity;

        return this;
    }
}
