package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.merkle.MerkleTree;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ajlopez on 03/08/2020.
 */
public class OrphanBlocksTest {
    @Test
    public void removeUnknownOrphan() {
        Block genesis = new Block(0, null, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE);
        Block block1 = new Block(1, genesis.getHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE);

        OrphanBlocks orphanBlocks = new OrphanBlocks();

        Assert.assertFalse(orphanBlocks.isKnownOrphan(block1.getHash()));

        orphanBlocks.removeOrphan(block1);

        Assert.assertFalse(orphanBlocks.isKnownOrphan(block1.getHash()));
    }
}
