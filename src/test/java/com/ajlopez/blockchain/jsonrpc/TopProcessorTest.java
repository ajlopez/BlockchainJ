package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajlopez on 02/12/2018.
 */
public class TopProcessorTest {
    // https://www.infoq.com/news/2009/07/junit-4.7-rules
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void unknownMethod() throws JsonRpcException {
        List<JsonValue> params = new ArrayList<>();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_foo", params);

        TopProcessor processor = new TopProcessor();

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Unknown method 'eth_foo'");
        processor.processRequest(request);
    }

    @Test
    public void delegateRequestToRegisteredProcessor() throws JsonRpcException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();

        List<JsonValue> params = new ArrayList<>();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_blockNumber", params);

        BlocksProcessor blocksProcessor = new BlocksProcessor(blockChain);
        TopProcessor processor = new TopProcessor();
        processor.registerProcess("eth_blockNumber", blocksProcessor);

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getVersion(), response.getVersion());
        Assert.assertEquals("\"0x00\"", response.getResult().toString());
    }
}
