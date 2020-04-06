package com.ajlopez.blockchain.merkle;

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
}
