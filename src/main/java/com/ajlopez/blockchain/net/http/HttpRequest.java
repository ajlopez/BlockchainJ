package com.ajlopez.blockchain.net.http;

import java.io.Reader;
import java.util.Map;

/**
 * Created by ajlopez on 03/12/2018.
 */
public class HttpRequest {
    private final String method;
    private final String resource;
    private final Map<String, String> headers;
    private final Reader reader;

    public HttpRequest(String method, String resource, Map<String, String> headers, Reader reader) {
        this.method = method;
        this.resource = resource;
        this.headers = headers;
        this.reader = reader;
    }

    public String getMethod() { return this.method; }

    public String getResource() { return this.resource; }

    public Map<String, String> getHeaders() { return this.headers; }

    public Reader getReader() { return this.reader; }
}
