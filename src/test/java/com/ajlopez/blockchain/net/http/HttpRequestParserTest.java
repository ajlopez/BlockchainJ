package com.ajlopez.blockchain.net.http;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Created by ajlopez on 03/12/2018.
 */
public class HttpRequestParserTest {
    @Test
    public void parseSimpleRequest() throws IOException {
        Reader reader = new StringReader("POST / HTTP 1.1\r\n\r\nfoo\r\n");
        HttpRequestParser parser = new HttpRequestParser();

        HttpRequest request = parser.parse(reader);

        Assert.assertNotNull(request);
        Assert.assertEquals("POST", request.getMethod());
        Assert.assertEquals("/", request.getResource());

        Assert.assertEquals('f', request.getReader().read());
    }
}
