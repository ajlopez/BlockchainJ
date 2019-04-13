package com.ajlopez.blockchain.net.http;

import com.ajlopez.blockchain.bc.BlockChain;
import com.ajlopez.blockchain.json.JsonLexerException;
import com.ajlopez.blockchain.json.JsonParserException;
import com.ajlopez.blockchain.jsonrpc.BlocksProcessor;
import com.ajlopez.blockchain.jsonrpc.JsonRpcException;
import com.ajlopez.blockchain.test.utils.FactoryHelper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by ajlopez on 05/12/2018.
 */
public class HttpProcessorTest {
    @Test
    public void processRequest() throws JsonLexerException, IOException, JsonRpcException, JsonParserException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();
        FactoryHelper.extendBlockChainWithBlocks(blockChain, 10);
        BlocksProcessor blocksProcessor = new BlocksProcessor(blockChain);

        String input = "POST /\r\n\r\n{ \"id\": 1, \"version\": \"2.0\", \"method\": \"eth_blockNumber\", \"params\": [] }";
        StringWriter writer = new StringWriter();

        HttpProcessor processor = new HttpProcessor(blocksProcessor,  new StringReader(input), writer);

        processor.process();

        String result = writer.toString();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals("200 OK\r\n\r\n{ \"id\": \"1\", \"version\": \"2.0\", \"result\": \"0x0a\" }", result);
    }

    @Test
    public void rejectGetRequest() throws JsonLexerException, IOException, JsonRpcException, JsonParserException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();
        FactoryHelper.extendBlockChainWithBlocks(blockChain, 10);
        BlocksProcessor blocksProcessor = new BlocksProcessor(blockChain);

        String input = "GET /\r\n\r\n";
        StringWriter writer = new StringWriter();

        HttpProcessor processor = new HttpProcessor(blocksProcessor,  new StringReader(input), writer);

        processor.process();

        String result = writer.toString();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals("404 ERROR\r\n\r\n", result);
    }

    @Test
    public void rejectPostRequestWithoutJsonObject() throws JsonLexerException, IOException, JsonRpcException, JsonParserException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();
        FactoryHelper.extendBlockChainWithBlocks(blockChain, 10);
        BlocksProcessor blocksProcessor = new BlocksProcessor(blockChain);

        String input = "POST /\r\n\r\n\"foo\"";
        StringWriter writer = new StringWriter();

        HttpProcessor processor = new HttpProcessor(blocksProcessor,  new StringReader(input), writer);

        processor.process();

        String result = writer.toString();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals("404 ERROR\r\n\r\n", result);
    }

    @Test
    public void rejectPostRequestWithoutIdProperty() throws JsonLexerException, IOException, JsonRpcException, JsonParserException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();
        FactoryHelper.extendBlockChainWithBlocks(blockChain, 10);
        BlocksProcessor blocksProcessor = new BlocksProcessor(blockChain);

        String input = "POST /\r\n\r\n{ \"version\": \"2.0\", \"method\": \"eth_blockNumber\", \"params\": [] }";
        StringWriter writer = new StringWriter();

        HttpProcessor processor = new HttpProcessor(blocksProcessor,  new StringReader(input), writer);

        processor.process();

        String result = writer.toString();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals("404 ERROR\r\n\r\n", result);
    }

    @Test
    public void rejectPostRequestWithoutVersionProperty() throws JsonLexerException, IOException, JsonRpcException, JsonParserException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();
        FactoryHelper.extendBlockChainWithBlocks(blockChain, 10);
        BlocksProcessor blocksProcessor = new BlocksProcessor(blockChain);

        String input = "POST /\r\n\r\n{ \"id\": 1, \"method\": \"eth_blockNumber\", \"params\": [] }";
        StringWriter writer = new StringWriter();

        HttpProcessor processor = new HttpProcessor(blocksProcessor,  new StringReader(input), writer);

        processor.process();

        String result = writer.toString();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals("404 ERROR\r\n\r\n", result);
    }

    @Test
    public void rejectPostRequestWithoutMethodProperty() throws JsonLexerException, IOException, JsonRpcException, JsonParserException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();
        FactoryHelper.extendBlockChainWithBlocks(blockChain, 10);
        BlocksProcessor blocksProcessor = new BlocksProcessor(blockChain);

        String input = "POST /\r\n\r\n{ \"id\": 1, \"version\": \"2.0\", \"params\": [] }";
        StringWriter writer = new StringWriter();

        HttpProcessor processor = new HttpProcessor(blocksProcessor,  new StringReader(input), writer);

        processor.process();

        String result = writer.toString();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals("404 ERROR\r\n\r\n", result);
    }

    @Test
    public void rejectPostRequestWithoutParamsProperty() throws JsonLexerException, IOException, JsonRpcException, JsonParserException {
        BlockChain blockChain = FactoryHelper.createBlockChainWithGenesis();
        FactoryHelper.extendBlockChainWithBlocks(blockChain, 10);
        BlocksProcessor blocksProcessor = new BlocksProcessor(blockChain);

        String input = "POST /\r\n\r\n{ \"id\": 1, \"version\": \"2.0\", \"method\": \"eth_blockNumber\" }";
        StringWriter writer = new StringWriter();

        HttpProcessor processor = new HttpProcessor(blocksProcessor,  new StringReader(input), writer);

        processor.process();

        String result = writer.toString();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals("404 ERROR\r\n\r\n", result);
    }
}
