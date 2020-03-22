package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.json.JsonConverter;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 02/12/2018.
 */
public class BlocksProviderTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void retrieveLatestBlock() throws JsonRpcException, IOException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();
        FactoryHelper.extendBlockChainWithBlocks(blockChain, 10);

        BlocksProvider provider = new BlocksProvider(blockChain);

        Block block = provider.getBlock("latest");

        Assert.assertNotNull(block);
        Assert.assertEquals(10, block.getNumber());
        Assert.assertEquals(blockChain.getBestBlock().getHash(), block.getHash());
    }

    @Test
    public void retrieveEarliestBlock() throws JsonRpcException, IOException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();
        FactoryHelper.extendBlockChainWithBlocks(blockChain, 10);

        BlocksProvider provider = new BlocksProvider(blockChain);

        Block block = provider.getBlock("earliest");

        Assert.assertNotNull(block);
        Assert.assertEquals(0, block.getNumber());
        Assert.assertEquals(blockChain.getBlockByNumber(0).getHash(), block.getHash());
    }

    @Test
    public void retrieveBlockByDecimalNumber() throws JsonRpcException, IOException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();
        FactoryHelper.extendBlockChainWithBlocks(blockChain, 10);

        BlocksProvider provider = new BlocksProvider(blockChain);

        Block block = provider.getBlock("7");

        Assert.assertNotNull(block);
        Assert.assertEquals(7, block.getNumber());
        Assert.assertEquals(blockChain.getBlockByNumber(7).getHash(), block.getHash());
    }

    @Test
    public void retrieveBlockByHexadecimalNumber() throws JsonRpcException, IOException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();
        FactoryHelper.extendBlockChainWithBlocks(blockChain, 20);

        BlocksProvider provider = new BlocksProvider(blockChain);

        Block block = provider.getBlock("0x0a");

        Assert.assertNotNull(block);
        Assert.assertEquals(10, block.getNumber());
        Assert.assertEquals(blockChain.getBlockByNumber(10).getHash(), block.getHash());
    }

    @Test
    public void pendingNotSupported() throws JsonRpcException, IOException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();
        FactoryHelper.extendBlockChainWithBlocks(blockChain, 20);

        BlocksProvider provider = new BlocksProvider(blockChain);

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Unsupported block id 'pending'");
        provider.getBlock("pending");
    }

    @Test
    public void invalidNumberFormat() throws JsonRpcException, IOException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();
        FactoryHelper.extendBlockChainWithBlocks(blockChain, 20);

        BlocksProvider provider = new BlocksProvider(blockChain);

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Invalid number format");
        provider.getBlock("foo");
    }
}
