package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.core.Block;
import com.ajlopez.blockchain.json.*;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
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
    public void blockNumberUsingBlockchainGenesis() throws JsonRpcException, IOException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();

        List<JsonValue> params = new ArrayList<>();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_blockNumber", params);

        BlocksProcessor processor = new BlocksProcessor(blockChain);

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getJsonRpc(), response.getJsonRpc());
        Assert.assertEquals("\"0x00\"", response.getResult().toString());
    }

    @Test
    public void blockNumberUsingBlockchainWithTenBlocks() throws JsonRpcException, IOException {
        BlockChain blockChain = createBlockchain(10, 0);

        List<JsonValue> params = new ArrayList<>();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_blockNumber", params);

        BlocksProcessor processor = new BlocksProcessor(blockChain);

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getJsonRpc(), response.getJsonRpc());
        Assert.assertEquals("\"0x0a\"", response.getResult().toString());
    }

    @Test
    public void getBlockByNumberUsingBlockchainWithTenBlocks() throws JsonRpcException, IOException {
        BlockChain blockChain = createBlockchain(10, 0);

        List<JsonValue> params = new ArrayList<>();
        params.add(new JsonNumericValue(10));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getBlockByNumber", params);

        BlocksProcessor processor = new BlocksProcessor(blockChain);

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getJsonRpc(), response.getJsonRpc());
        Assert.assertEquals(JsonValueType.OBJECT, response.getResult().getType());

        JsonObjectValue jovalue = (JsonObjectValue)response.getResult();

        Assert.assertTrue(jovalue.hasProperty("number"));
        Assert.assertEquals("10", jovalue.getProperty("number").getValue());
    }

    @Test
    public void getBlockByHexadecimalNumberUsingBlockchainWithTenBlocks() throws JsonRpcException, IOException {
        BlockChain blockChain = createBlockchain(10, 0);

        List<JsonValue> params = new ArrayList<>();
        params.add(new JsonStringValue("0x0a"));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getBlockByNumber", params);

        BlocksProcessor processor = new BlocksProcessor(blockChain);

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getJsonRpc(), response.getJsonRpc());
        Assert.assertEquals(JsonValueType.OBJECT, response.getResult().getType());

        JsonObjectValue jovalue = (JsonObjectValue)response.getResult();

        Assert.assertTrue(jovalue.hasProperty("number"));
        Assert.assertEquals("10", jovalue.getProperty("number").getValue());
    }

    @Test
    public void getUnknownBlockByHexadecimalNumber() throws JsonRpcException, IOException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();

        List<JsonValue> params = new ArrayList<>();
        params.add(new JsonStringValue("0x20"));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getBlockByNumber", params);

        BlocksProcessor processor = new BlocksProcessor(blockChain);

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getJsonRpc(), response.getJsonRpc());
        Assert.assertEquals(JsonValueType.NULL, response.getResult().getType());
    }

    @Test
    public void getBlockByHashUsingBlockchainWithTenBlocks() throws JsonRpcException, IOException {
        BlockChain blockChain = createBlockchain(10, 0);

        Block block = blockChain.getBlockByNumber(3);

        List<JsonValue> params = new ArrayList<>();
        params.add(new JsonStringValue(block.getHash().toString()));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getBlockByHash", params);

        BlocksProcessor processor = new BlocksProcessor(blockChain);

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getJsonRpc(), response.getJsonRpc());
        Assert.assertEquals(JsonValueType.OBJECT, response.getResult().getType());

        JsonObjectValue jovalue = (JsonObjectValue)response.getResult();

        Assert.assertTrue(jovalue.hasProperty("number"));
        Assert.assertEquals("3", jovalue.getProperty("number").getValue());
        Assert.assertEquals(block.getHash().toString(), jovalue.getProperty("hash").getValue());
    }

    @Test
    public void getBlockByHashUsingBlockchainWithTenBlocksWithTransactions() throws JsonRpcException, IOException {
        BlockChain blockChain = createBlockchain(10, 10);

        Block block = blockChain.getBlockByNumber(10);

        List<JsonValue> params = new ArrayList<>();
        params.add(new JsonStringValue(block.getHash().toString()));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getBlockByHash", params);

        BlocksProcessor processor = new BlocksProcessor(blockChain);

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getJsonRpc(), response.getJsonRpc());
        Assert.assertEquals(JsonValueType.OBJECT, response.getResult().getType());

        JsonObjectValue jovalue = (JsonObjectValue)response.getResult();

        Assert.assertTrue(jovalue.hasProperty("number"));
        Assert.assertEquals("10", jovalue.getProperty("number").getValue());
        Assert.assertEquals(block.getHash().toString(), jovalue.getProperty("hash").getValue());
        Assert.assertEquals(10, ((JsonArrayValue)jovalue.getProperty("transactions")).size());

        for (int k = 0; k < 10; k++)
            Assert.assertEquals(block.getTransactions().get(k).getHash().toString(), ((JsonArrayValue)jovalue.getProperty("transactions")).getValue(k).getValue());
    }

    @Test
    public void getBlockByNumberUsingBlockchainWithTenBlocksWithTransactions() throws JsonRpcException, IOException {
        BlockChain blockChain = createBlockchain(10, 10);

        Block block = blockChain.getBlockByNumber(10);

        List<JsonValue> params = new ArrayList<>();
        params.add(new JsonStringValue("10"));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getBlockByNumber", params);

        BlocksProcessor processor = new BlocksProcessor(blockChain);

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getJsonRpc(), response.getJsonRpc());
        Assert.assertEquals(JsonValueType.OBJECT, response.getResult().getType());

        JsonObjectValue jovalue = (JsonObjectValue)response.getResult();

        Assert.assertTrue(jovalue.hasProperty("number"));
        Assert.assertEquals("10", jovalue.getProperty("number").getValue());
        Assert.assertEquals(block.getHash().toString(), jovalue.getProperty("hash").getValue());
        Assert.assertEquals(10, ((JsonArrayValue)jovalue.getProperty("transactions")).size());

        for (int k = 0; k < 10; k++)
            Assert.assertEquals(block.getTransactions().get(k).getHash().toString(), ((JsonArrayValue)jovalue.getProperty("transactions")).getValue(k).getValue());
    }

    @Test
    public void getBlockByNumberUsingBlockchainWithTenBlocksWithFullTransactions() throws JsonRpcException, IOException {
        BlockChain blockChain = createBlockchain(10, 10);

        Block block = blockChain.getBlockByNumber(10);

        List<JsonValue> params = new ArrayList<>();
        params.add(new JsonStringValue("10"));
        params.add(new JsonBooleanValue(true));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getBlockByNumber", params);

        BlocksProcessor processor = new BlocksProcessor(blockChain);

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getJsonRpc(), response.getJsonRpc());
        Assert.assertEquals(JsonValueType.OBJECT, response.getResult().getType());

        JsonObjectValue jovalue = (JsonObjectValue)response.getResult();

        Assert.assertTrue(jovalue.hasProperty("number"));
        Assert.assertEquals("10", jovalue.getProperty("number").getValue());
        Assert.assertEquals(block.getHash().toString(), jovalue.getProperty("hash").getValue());
        Assert.assertEquals(10, ((JsonArrayValue)jovalue.getProperty("transactions")).size());

        for (int k = 0; k < 10; k++)
            Assert.assertEquals(block.getTransactions().get(k).getHash().toString(), ((JsonObjectValue)((JsonArrayValue)jovalue.getProperty("transactions")).getValue(k)).getProperty("hash").getValue());
    }

    @Test
    public void getBlockByHashUsingBlockchainWithTenBlocksWithFullTransactions() throws JsonRpcException, IOException {
        BlockChain blockChain = createBlockchain(10, 10);

        Block block = blockChain.getBlockByNumber(10);

        List<JsonValue> params = new ArrayList<>();
        params.add(new JsonStringValue(block.getHash().toString()));
        params.add(new JsonBooleanValue(true));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getBlockByHash", params);

        BlocksProcessor processor = new BlocksProcessor(blockChain);

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getJsonRpc(), response.getJsonRpc());
        Assert.assertEquals(JsonValueType.OBJECT, response.getResult().getType());

        JsonObjectValue jovalue = (JsonObjectValue)response.getResult();

        Assert.assertTrue(jovalue.hasProperty("number"));
        Assert.assertEquals("10", jovalue.getProperty("number").getValue());
        Assert.assertEquals(block.getHash().toString(), jovalue.getProperty("hash").getValue());
        Assert.assertEquals(10, ((JsonArrayValue)jovalue.getProperty("transactions")).size());

        for (int k = 0; k < 10; k++)
            Assert.assertEquals(block.getTransactions().get(k).getHash().toString(), ((JsonObjectValue)((JsonArrayValue)jovalue.getProperty("transactions")).getValue(k)).getProperty("hash").getValue());
    }

    @Test
    public void getUnknownBlockByHash() throws JsonRpcException, IOException {
        BlockChain blockChain = createBlockchain(10, 0);

        List<JsonValue> params = new ArrayList<>();
        params.add(new JsonStringValue(FactoryHelper.createRandomBlockHash().toString()));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getBlockByHash", params);

        BlocksProcessor processor = new BlocksProcessor(blockChain);

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getJsonRpc(), response.getJsonRpc());
        Assert.assertEquals(JsonValueType.NULL, response.getResult().getType());
    }

    @Test
    public void getLatestBlockUsingBlockchainWithTenBlocks() throws JsonRpcException, IOException {
        BlockChain blockChain = createBlockchain(10, 0);

        List<JsonValue> params = new ArrayList<>();
        params.add(new JsonStringValue("latest"));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getBlockByNumber", params);

        BlocksProcessor processor = new BlocksProcessor(blockChain);

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getJsonRpc(), response.getJsonRpc());
        Assert.assertEquals(JsonValueType.OBJECT, response.getResult().getType());

        JsonObjectValue jovalue = (JsonObjectValue)response.getResult();

        Assert.assertTrue(jovalue.hasProperty("number"));
        Assert.assertEquals("10", jovalue.getProperty("number").getValue());
    }

    @Test
    public void getEarliestBlockUsingBlockchainWithTenBlocks() throws JsonRpcException, IOException {
        BlockChain blockChain = createBlockchain(10, 0);

        List<JsonValue> params = new ArrayList<>();
        params.add(new JsonStringValue("earliest"));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_getBlockByNumber", params);

        BlocksProcessor processor = new BlocksProcessor(blockChain);

        JsonRpcResponse response = processor.processRequest(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(request.getId(), response.getId());
        Assert.assertEquals(request.getJsonRpc(), response.getJsonRpc());
        Assert.assertEquals(JsonValueType.OBJECT, response.getResult().getType());

        JsonObjectValue jovalue = (JsonObjectValue)response.getResult();

        Assert.assertTrue(jovalue.hasProperty("number"));
        Assert.assertEquals("0", jovalue.getProperty("number").getValue());
    }

    @Test
    public void unknownMethod() throws JsonRpcException, IOException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();

        List<JsonValue> params = new ArrayList<>();
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_foo", params);

        BlocksProcessor processor = new BlocksProcessor(blockChain);

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Unknown method 'eth_foo'");
        processor.processRequest(request);
    }

    @Test
    public void invalidNumberOfParameters() throws JsonRpcException, IOException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();

        List<JsonValue> params = Collections.singletonList(JsonConverter.convert("foo"));
        JsonRpcRequest request =  new JsonRpcRequest("1", "2.0", "eth_blockNumber", params);

        BlocksProcessor processor = new BlocksProcessor(blockChain);

        exception.expect(JsonRpcException.class);
        exception.expectMessage("Invalid number of parameters: expected 0 found 1");
        processor.processRequest(request);
    }

    private static BlockChain createBlockchain(int nblocks, int ntransactions) throws IOException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();
        FactoryHelper.extendBlockChainWithBlocks(blockChain, nblocks - 1);
        Block topBlock = FactoryHelper.createBlock(blockChain.getBlockByNumber(9), FactoryHelper.createRandomAddress(), ntransactions);
        Assert.assertEquals(ntransactions, topBlock.getTransactionsCount());

        Assert.assertTrue(blockChain.connectBlock(topBlock));

        return blockChain;
    }
}
