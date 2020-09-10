package com.ajlopez.blockchain.processors;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.Address;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.merkle.MerkleTree;
import com.ajlopez.blockchain.state.Trie;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by ajlopez on 03/08/2020.
 */
public class OrphanBlocksTest {
    @Test
    public void addOrphan() {
        Block genesis = new Block(0, null, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE, 0);
        Block block1 = new Block(1, genesis.getHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE, 0);

        OrphanBlocks orphanBlocks = new OrphanBlocks();

        orphanBlocks.addToOrphans(block1);

        Assert.assertTrue(orphanBlocks.isKnownOrphan(block1.getHash()));
    }

    @Test
    public void addChildrenOrphans() {
        Block genesis = new Block(0, null, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE, 0);
        Block block1 = new Block(1, genesis.getHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE, 0);
        Block block2 = new Block(2, block1.getHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE, 0);
        Block block2b = new Block(2, block1.getHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.TWO, 0);
        Block block3 = new Block(3, block2.getHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE, 0);
        Block block4 = new Block(4, block3.getHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE, 0);
        Block block5 = new Block(5, block4.getHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE, 0);

        OrphanBlocks orphanBlocks = new OrphanBlocks();

        orphanBlocks.addToOrphans(block1);
        orphanBlocks.addToOrphans(block2);
        orphanBlocks.addToOrphans(block3);
        orphanBlocks.addToOrphans(block2b);
        orphanBlocks.addToOrphans(block5);

        List<Block> result = orphanBlocks.getChildrenOrphanBlocks(block1);

        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(block2));
        Assert.assertTrue(result.contains(block2b));
    }

    @Test
    public void removeUnknownOrphan() {
        Block genesis = new Block(0, null, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE, 0);
        Block block1 = new Block(1, genesis.getHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE, 0);

        OrphanBlocks orphanBlocks = new OrphanBlocks();

        Assert.assertFalse(orphanBlocks.isKnownOrphan(block1.getHash()));

        orphanBlocks.removeOrphan(block1);

        Assert.assertFalse(orphanBlocks.isKnownOrphan(block1.getHash()));
    }

    @Test
    public void removeKnownOrphan() {
        Block genesis = new Block(0, null, null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE, 0);
        Block block1 = new Block(1, genesis.getHash(), MerkleTree.EMPTY_MERKLE_TREE_HASH, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, Address.ZERO, Difficulty.ONE, 0);

        OrphanBlocks orphanBlocks = new OrphanBlocks();

        orphanBlocks.addToOrphans(block1);

        Assert.assertTrue(orphanBlocks.isKnownOrphan(block1.getHash()));

        orphanBlocks.removeOrphan(block1);

        Assert.assertFalse(orphanBlocks.isKnownOrphan(block1.getHash()));
    }
}
