package com.ajlopez.blockchain.net.http;

import com.ajlopez.blockchain.json.*;
import com.ajlopez.blockchain.jsonrpc.JsonRpcRequest;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Created by ajlopez on 04/12/2018.
 */
public class HttpProcessor {
    private final Reader reader;
    private final Writer writer;

    public HttpProcessor(Reader reader, Writer writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public void process() throws JsonLexerException, JsonParserException, IOException {
        HttpRequestParser parser = new HttpRequestParser();

        HttpRequest request = parser.parse(this.reader);

        if (request.getMethod().equals("POST")) {
            JsonParser jparser = new JsonParser(this.reader);
            JsonValue jvalue = jparser.parseValue();

            if (jvalue.getType() == JsonValueType.OBJECT) {
                JsonObjectValue jovalue = (JsonObjectValue) jvalue;

                if (jovalue.hasProperty("method") && jovalue.hasProperty("id") && jovalue.hasProperty("version") && jovalue.hasProperty("params") && jovalue.getProperty("params").getType() == JsonValueType.ARRAY) {
                    // TODO
                }
            }
        }
    }
}

