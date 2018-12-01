package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.json.JsonConverter;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 30/11/2018.
 */
public class BlocksProcessorTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void blockNumberUsingBlockchainGenesis() throws JsonRpcException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();

        List<JsonValue> params = new ArrayList<>();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_blockNumber", params);

        BlocksProcessor processor = new BlocksProcessor(blockChain);

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getVersion(), response.getVersion());
        Assert.assertEquals("\"0x00\"", response.getResult().toString());
    }

    @Test
    public void blockNumberUsingBlockchainWithTenBlocks() throws JsonRpcException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();
        FactoryHelper.extendBlockChainWithBlocks(blockChain, 10);

        List<JsonValue> params = new ArrayList<>();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_blockNumber", params);

        BlocksProcessor processor = new BlocksProcessor(blockChain);

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getVersion(), response.getVersion());
        Assert.assertEquals("\"0x0a\"", response.getResult().toString());
    }

    @Test
    public void unknownMethod() throws JsonRpcException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();

        List<JsonValue> params = new ArrayList<>();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_foo", params);

        BlocksProcessor processor = new BlocksProcessor(blockChain);

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Unknown method 'eth_foo'");
        processor.processRequest(request);
    }

    @Test
    public void invalidNumberOfParameters() throws JsonRpcException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();

        List<JsonValue> params = Collections.singletonList(JsonConverter.convert("foo"));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_blockNumber", params);

        BlocksProcessor processor = new BlocksProcessor(blockChain);

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Invalid number of parameters: expected 0 found 1");
        processor.processRequest(request);
    }
}
