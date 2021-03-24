package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.json.JsonConverter;
import com.ajlopez.blockchain.json.JsonLexerException;
import com.ajlopez.blockchain.json.JsonParserException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by ajlopez on 30/11/2018.
 */
public class JsonRpcResponseTest {
    @Test
    public void createWithConstructor() throws JsonParserException, IOException, JsonLexerException {
        JsonRpcResponse response = new JsonRpcResponse("42", "2.0", JsonConverter.convert("foo"), null);

        Assert.assertEquals("42", response.getId());
        Assert.assertEquals("2.0", response.getJsonRpc());
        Assert.assertEquals("\"foo\"", response.getResult().toString());
    }
}
