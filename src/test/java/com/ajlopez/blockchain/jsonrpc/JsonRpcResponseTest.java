package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.json.JsonConverter;
import com.ajlopez.blockchain.json.LexerException;
import com.ajlopez.blockchain.json.ParserException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Created by ajlopez on 30/11/2018.
 */
public class JsonRpcResponseTest {
    @Test
    public void createWithConstructor() throws ParserException, IOException, LexerException {
        JsonRpcResponse response = new JsonRpcResponse("42", "2.0", JsonConverter.convert("foo"));

        Assert.assertEquals("42", response.getId());
        Assert.assertEquals("2.0", response.getVersion());
        Assert.assertEquals("\"foo\"", response.getResult().toString());
    }
}