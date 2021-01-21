package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.store.HashMapStore;
import com.ajlopez.blockchain.store.MemoryStores;
import com.ajlopez.blockchain.store.Stores;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

/**
 * Created by ajlopez on 19/01/2021.
 */
public class BlockUtilsTest {
    @Test
    public void getGenesisAncestorsAsEmptySet() throws IOException {
        Block genesis = GenesisGenerator.generateGenesis();

        Set<BlockHeader> result = BlockUtils.getAncestorsHeaders(genesis, 0, null);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void getFiveAncestorsSet() throws IOException {
        Stores stores = new MemoryStores();
        BlockStore blockStore = stores.getBlockStore();
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis(stores);
        FactoryHelper.extendBlockChainWithBlocks(blockChain, 10);

        Block block10 = blockChain.getBlockByNumber(10);

        Set<BlockHeader> result = BlockUtils.getAncestorsHeaders(block10, 5, blockStore);

        Assert.assertNotNull(result);
        Assert.assertEquals(5, result.size());

        for (int k = 5; k < 10; k++)
            Assert.assertTrue(result.contains(blockChain.getBlockByNumber(k).getHeader()));
    }

    @Test
    public void getTenAncestorsSet() throws IOException {
        Stores stores = new MemoryStores();
        BlockStore blockStore = stores.getBlockStore();
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis(stores);
        FactoryHelper.extendBlockChainWithBlocks(blockChain, 10);

        Block block10 = blockChain.getBlockByNumber(10);

        Set<BlockHeader> result = BlockUtils.getAncestorsHeaders(block10, 10, blockStore);

        Assert.assertNotNull(result);
        Assert.assertEquals(10, result.size());

        for (int k = 0; k < 10; k++)
            Assert.assertTrue(result.contains(blockChain.getBlockByNumber(k).getHeader()));
    }

    @Test
    public void getTooMuchAncestorsSet() throws IOException {
        Stores stores = new MemoryStores();
        BlockStore blockStore = stores.getBlockStore();
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis(stores);
        FactoryHelper.extendBlockChainWithBlocks(blockChain, 10);

        Block block10 = blockChain.getBlockByNumber(10);

        Set<BlockHeader> result = BlockUtils.getAncestorsHeaders(block10, 20, blockStore);

        Assert.assertNotNull(result);
        Assert.assertEquals(10, result.size());

        for (int k = 0; k < 10; k++)
            Assert.assertTrue(result.contains(blockChain.getBlockByNumber(k).getHeader()));
    }
}
