package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.json.JsonBuilder;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajlopez on 30/11/2018.
 */
public class BlocksProcessorTest {
    @Test
    public void blockNumber() {
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
}
