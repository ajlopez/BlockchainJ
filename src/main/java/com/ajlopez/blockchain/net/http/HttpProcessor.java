package com.ajlopez.blockchain.net.http;

import com.ajlopez.blockchain.json.*;
import com.ajlopez.blockchain.jsonrpc.JsonRpcException;
import com.ajlopez.blockchain.jsonrpc.JsonRpcProcessor;
import com.ajlopez.blockchain.jsonrpc.JsonRpcRequest;
import com.ajlopez.blockchain.jsonrpc.JsonRpcResponse;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * Created by ajlopez on 04/12/2018.
 */
public class HttpProcessor {
    private final JsonRpcProcessor jsonRpcProcessor;
    private final Reader reader;
    private final Writer writer;

    public HttpProcessor(JsonRpcProcessor jsonRpcProcessor, Reader reader, Writer writer) {
        this.jsonRpcProcessor = jsonRpcProcessor;
        this.reader = reader;
        this.writer = writer;
    }

    public void process() throws JsonLexerException, JsonParserException, IOException {
        HttpRequestParser parser = new HttpRequestParser();

        HttpRequest request = parser.parse(this.reader);

        if (!"POST".equals(request.getMethod())) {
            this.reject();
            return;
        }

        JsonParser jparser = new JsonParser(request.getReader());
        JsonValue jvalue = jparser.parseValue();

        if (jvalue == null || jvalue.getType() != JsonValueType.OBJECT) {
            this.reject();
            return;
        }

        JsonObjectValue jovalue = (JsonObjectValue) jvalue;

        if (!jovalue.hasProperty("method") || !jovalue.hasProperty("id") || !jovalue.hasProperty("jsonrpc") || !jovalue.hasProperty("params") || jovalue.getProperty("params").getType() != JsonValueType.ARRAY) {
            this.reject();
            return;
        }

        String id = jovalue.getProperty("id").getValue().toString();
        String jsonrpc = jovalue.getProperty("jsonrpc").getValue().toString();
        String method = jovalue.getProperty("method").getValue().toString();

        JsonArrayValue avalue = (JsonArrayValue)jovalue.getProperty("params");
        List<JsonValue> params = avalue.getValues();

        JsonRpcRequest jsonrequest = new JsonRpcRequest(id, jsonrpc, method, params);

        JsonRpcResponse jsonresponse;

        try {
            jsonresponse = this.jsonRpcProcessor.processRequest(jsonrequest);
        }
        catch (JsonRpcException ex) {
            jsonresponse = JsonRpcResponse.createResponseWithError(jsonrequest, ex);
        }

        JsonBuilder builder = new JsonBuilder();
        builder = builder.object()
                .name("id")
                .value(jsonresponse.getId())
                .name("jsonrpc")
                .value(jsonresponse.getJsonRpc());

        if (jsonresponse.getError() != null)
            builder = builder.name("error")
                    .value(jsonresponse.getError());
        else
            builder = builder.name("result")
                    .value(jsonresponse.getResult());

        JsonValue response = builder.build();

        this.writer.write("HTTP/1.1 200 OK\r\n\r\n");
        JsonWriter jsonWriter = new JsonWriter(this.writer);
        jsonWriter.write(response);
        this.writer.flush();
    }

    private void reject() throws IOException {
        this.writer.write("HTTP/1.1 404 ERROR\r\n\r\n");
        this.writer.flush();
    }
}

