package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.types.BlockHash;
import com.ajlopez.blockchain.core.types.Difficulty;
import com.ajlopez.blockchain.encoding.BlockEncoder;
import com.ajlopez.blockchain.state.Trie;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class BlockStoreTest {
    @Test
    public void retrieveUnknowBlockAsNull() throws IOException {
        BlockStore blockStore = new BlockStore(new HashMapStore());
        BlockHash hash = FactoryHelper.createRandomBlockHash();

        Assert.assertNull(blockStore.getBlock(hash));
    }

    @Test
    public void retrieveBlockAsNull() throws IOException {
        Block block = new Block(1, FactoryHelper.createRandomBlockHash(), null, Trie.EMPTY_TRIE_HASH, System.currentTimeMillis() / 1000, FactoryHelper.createRandomAddress(), Difficulty.ONE, 0, 0, null, 0);
        BlockStore blockStore = new BlockStore(new HashMapStore());
        BlockHash hash = block.getHash();

        blockStore.saveBlock(block);

        Block result = blockStore.getBlock(hash);

        Assert.assertNotNull(result);
        Assert.assertArrayEquals(BlockEncoder.encode(block), BlockEncoder.encode(result));
    }
}
