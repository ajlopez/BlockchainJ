package com.ajlopez.blockchain.bc;

import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.core.BlockHeader;
import com.ajlopez.blockchain.store.MemoryStores;
import com.ajlopez.blockchain.store.Stores;
import com.ajlopez.blockchain.test.World;
import com.ajlopez.blockchain.test.dsl.DslException;
import com.ajlopez.blockchain.test.dsl.DslParser;
import com.ajlopez.blockchain.test.dsl.WorldDslProcessor;
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

        BlockBuilder unclesBuilder =  new BlockBuilder().parent(genesis);
        BlockHeader uncle1 = unclesBuilder.buildHeader();
        BlockHeader uncle2 = unclesBuilder.buildHeader();

        List<BlockHeader> uncles = new ArrayList<>();
        uncles.add(uncle1);
        uncles.add(uncle2);

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
        Assert.assertTrue(result.contains(uncle1));
        Assert.assertTrue(result.contains(uncle2));
    }

    @Test
    public void getAncestorsAllHeadersSet() throws IOException {
        Stores stores = new MemoryStores();
        BlockStore blockStore = stores.getBlockStore();

        Block genesis = new BlockBuilder().number(0).build();
        Block block1 = new BlockBuilder().parent(genesis).build();

        BlockBuilder unclesBuilder =  new BlockBuilder().parent(genesis);
        BlockHeader uncle1 = unclesBuilder.buildHeader();
        BlockHeader uncle2 = unclesBuilder.buildHeader();

        List<BlockHeader> uncles = new ArrayList<>();
        uncles.add(uncle1);
        uncles.add(uncle2);

        Block block2 = new BlockBuilder().parent(block1).uncles(uncles).build();
        Block block3 = new BlockBuilder().parent(block2).build();

        blockStore.saveBlock(genesis);
        blockStore.saveBlock(block1);
        blockStore.saveBlock(block2);
        blockStore.saveBlock(block3);

        Set<BlockHeader> result = BlockUtils.getAncestorsAllHeaders(block3, 3, blockStore);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());

        Assert.assertEquals(5, result.size());
        Assert.assertTrue(result.contains(uncle1));
        Assert.assertTrue(result.contains(uncle2));
        Assert.assertTrue(result.contains(genesis.getHeader()));
        Assert.assertTrue(result.contains(block1.getHeader()));
        Assert.assertTrue(result.contains(block2.getHeader()));
    }

    @Test
    public void getAncestorsAllHeadersSetUsingDslFile() throws IOException, DslException {
        DslParser parser = DslParser.fromResource("dsl/blockchain06.txt");
        World world = new World();
        WorldDslProcessor processor = new WorldDslProcessor(world);
        processor.processCommands(parser);

        BlockStore blockStore = world.getBlockStore();

        Block genesis = world.getBlock("genesis");
        Block block1 = world.getBlock("b1");
        Block block1b = world.getBlock("b1b");
        Block block1c = world.getBlock("b1c");
        Block block2plus = world.getBlock("b2plus");
        Block block3plus = world.getBlock("b3plus");

        Set<BlockHeader> result = BlockUtils.getAncestorsAllHeaders(block3plus, 3, blockStore);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());

        Assert.assertEquals(5, result.size());
        Assert.assertTrue(result.contains(block2plus.getHeader()));
        Assert.assertTrue(result.contains(block1.getHeader()));
        Assert.assertTrue(result.contains(genesis.getHeader()));
        Assert.assertTrue(result.contains(block1b.getHeader()));
        Assert.assertTrue(result.contains(block1c.getHeader()));
    }

    @Test
    public void getPreviousAllHeadersSetUsingDslFile() throws IOException, DslException {
        DslParser parser = DslParser.fromResource("dsl/blockchain06.txt");
        World world = new World();
        WorldDslProcessor processor = new WorldDslProcessor(world);
        processor.processCommands(parser);

        BlockStore blockStore = world.getBlockStore();
        BlocksInformationStore blocksInformationStore = world.getBlocksInformationStore();

        Block genesis = world.getBlock("genesis");
        Block block1 = world.getBlock("b1");
        Block block1b = world.getBlock("b1b");
        Block block1c = world.getBlock("b1c");
        Block block1d = world.getBlock("b1d");
        Block block2 = world.getBlock("b2");
        Block block2plus = world.getBlock("b2plus");
        Block block3plus = world.getBlock("b3plus");

        Set<BlockHeader> result = BlockUtils.getPreviousAllHeaders(block3plus, 3, blockStore, blocksInformationStore);

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());

        Assert.assertEquals(7, result.size());
        Assert.assertTrue(result.contains(block2plus.getHeader()));
        Assert.assertTrue(result.contains(block1.getHeader()));
        Assert.assertTrue(result.contains(genesis.getHeader()));
        Assert.assertTrue(result.contains(block1b.getHeader()));
        Assert.assertTrue(result.contains(block1c.getHeader()));
        Assert.assertTrue(result.contains(block1d.getHeader()));
        Assert.assertTrue(result.contains(block2.getHeader()));
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
