package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.json.LexerException;
import com.ajlopez.blockchain.json.ParserException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Created by ajlopez on 10/11/2018.
 */
public class JsonRpcRequestTest {
    @Test
    public void createSimpleRequestFromStringReader() throws ParserException, IOException, LexerException {
        String text = "{ \"id\": 1, \"jsonrpc\": \"2.0\", \"method\": \"eth_blockNumber\", \"params\": [] } ";
        Reader reader = new StringReader(text);

        JsonRpcRequest request = JsonRpcRequest.fromReader(reader);

        Assert.assertNotNull(request);
        Assert.assertEquals("1", request.getId());
        Assert.assertEquals("2.0", request.getVersion());
        Assert.assertEquals("eth_blockNumber", request.getMethod());
        Assert.assertNotNull(request.getParams());
        Assert.assertTrue(request.getParams().isEmpty());
    }
}
