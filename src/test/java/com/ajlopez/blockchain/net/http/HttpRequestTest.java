package com.ajlopez.blockchain.net.http;

import org.junit.Assert;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

/**
 * Created by ajlopez on 03/12/2018.
 */
public class HttpRequestTest {
    @Test
    public void createRequest() {
        String method = "POST";
        String resource = "/";
        Reader reader = new StringReader("foo");

        HttpRequest request = new HttpRequest(method, resource, reader);

        Assert.assertEquals(method, request.getMethod());
        Assert.assertEquals(resource, request.getResource());
        Assert.assertEquals(reader, request.getReader());
    }
}
