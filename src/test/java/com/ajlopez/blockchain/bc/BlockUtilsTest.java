package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.store.MemoryStores;
import com.ajlopez.blockchain.store.Stores;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    public void getEmptyAncestorUnclesSet() throws IOException {
        Stores stores = new MemoryStores();
        BlockStore blockStore = stores.getBlockStore();
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis(stores);
        FactoryHelper.extendBlockChainWithBlocks(blockChain, 10);

        Block block10 = blockChain.getBlockByNumber(10);

        Set<BlockHeader> result = BlockUtils.getAncestorsUncles(block10, 5, blockStore);

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void getAncestorUnclesSet() throws IOException {
        Stores stores = new MemoryStores();
        BlockStore blockStore = stores.getBlockStore();

        Block genesis = new BlockBuilder().number(0).build();
        Block block1 = new BlockBuilder().parent(genesis).build();
        Block uncle1 = new BlockBuilder().parent(genesis).build();
        Block uncle2 = new BlockBuilder().parent(genesis).build();

        List<BlockHeader> uncles = new ArrayList<>();
        uncles.add(uncle1.getHeader());
        uncles.add(uncle2.getHeader());

        Block block2 = new BlockBuilder().parent(block1).uncles(uncles).build();
        Block block3 = new BlockBuilder().parent(block2).build();

        blockStore.saveBlock(genesis);
        blockStore.saveBlock(block1);
        blockStore.saveBlock(block2);
        blockStore.saveBlock(block3);

        Set<BlockHeader> result = BlockUtils.getAncestorsUncles(block3, 3, blockStore);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains(uncle1.getHeader()));
        Assert.assertTrue(result.contains(uncle2.getHeader()));
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
