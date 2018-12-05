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

        String input = "POST /\r\n\r\n{ \"id\": 1, \"version\": \"2.0\", \"method\": \"eth_blockNumber\", \"params:\": [] }";
        StringWriter writer = new StringWriter();

        HttpProcessor processor = new HttpProcessor(blocksProcessor,  new StringReader(input), writer);

        processor.process();

        String result = writer.toString();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
    }
}
