package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.json.*;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Created by ajlopez on 10/11/2018.
 */
public class JsonRpcRequest {
    private final String id;
    private final String version;
    private final String method;
    private final List<JsonValue> params;

    public static JsonRpcRequest fromReader(Reader reader) throws ParserException, IOException, LexerException {
        JsonObjectValue json = (JsonObjectValue)(new Parser(reader)).parseValue();

        return new JsonRpcRequest(
                json.getProperty("id").getValue().toString(),
                json.getProperty("jsonrpc").getValue().toString(),
                json.getProperty("method").getValue().toString(),
                ((JsonArrayValue)json.getProperty("params")).getValues()
        );
    }

    public JsonRpcRequest(String id, String version, String method, List<JsonValue> params) {
        this.id = id;
        this.version = version;
        this.method = method;
        this.params = params;
    }

    public String getId() {
        return this.id;
    }

    public String getVersion() {
        return this.version;
    }

    public String getMethod() {
        return this.method;
    }

    public List<JsonValue> getParams() {
        return this.params;
    }

    public boolean check(String method, int arity) {
        if (!this.method.equals(method))
            return false;

        if (arity != this.params.size())
            throw new UnsupportedOperationException(String.format("Invalid number of parameters: expected %d found %d", arity, this.params.size()));

        return true;
    }
}
