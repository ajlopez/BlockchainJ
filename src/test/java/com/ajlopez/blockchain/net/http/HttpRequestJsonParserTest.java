package com.ajlopez.blockchain.net.http;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Created by ajlopez on 03/12/2018.
 */
public class HttpRequestJsonParserTest {
    @Test
    public void parseSimpleRequest() throws IOException {
        Reader reader = new StringReader("POST / HTTP 1.1\r\n\r\nfoo\r\n");
        HttpRequestParser parser = new HttpRequestParser();

        HttpRequest request = parser.parse(reader);

        Assert.assertNotNull(request);
        Assert.assertEquals("POST", request.getMethod());
        Assert.assertEquals("/", request.getResource());

        Assert.assertNotNull(request.getHeaders());
        Assert.assertTrue(request.getHeaders().isEmpty());

        Assert.assertEquals('f', request.getReader().read());
    }

    @Test
    public void parseRequestWithHeaders() throws IOException {
        Reader reader = new StringReader("POST / HTTP 1.1\r\nContent-type: application/json; charset: UTF8\r\nHeader 2: Value 2\r\n\nfoo\r\n");
        HttpRequestParser parser = new HttpRequestParser();

        HttpRequest request = parser.parse(reader);

        Assert.assertNotNull(request);
        Assert.assertEquals("POST", request.getMethod());
        Assert.assertEquals("/", request.getResource());

        Assert.assertNotNull(request.getHeaders());
        Assert.assertEquals("application/json; charset: UTF8", request.getHeaders().get("Content-type"));
        Assert.assertEquals("Value 2", request.getHeaders().get("Header 2"));

        Assert.assertEquals('f', request.getReader().read());
    }
}
