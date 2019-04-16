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
    private final String jsonrpc;
    private final String method;
    private final List<JsonValue> params;

    public static JsonRpcRequest fromReader(Reader reader) throws JsonParserException, IOException, JsonLexerException {
        JsonObjectValue json = (JsonObjectValue)(new JsonParser(reader)).parseValue();

        return new JsonRpcRequest(
                json.getProperty("id").getValue().toString(),
                json.getProperty("jsonrpc").getValue().toString(),
                json.getProperty("method").getValue().toString(),
                ((JsonArrayValue)json.getProperty("params")).getValues()
        );
    }

    public JsonRpcRequest(String id, String jsonrpc, String method, List<JsonValue> params) {
        this.id = id;
        this.jsonrpc = jsonrpc;
        this.method = method;
        this.params = params;
    }

    public String getId() {
        return this.id;
    }

    public String getJsonRpc() {
        return this.jsonrpc;
    }

    public String getMethod() {
        return this.method;
    }

    public List<JsonValue> getParams() {
        return this.params;
    }

    public boolean check(String method, int arity) throws JsonRpcException {
        if (!this.method.equals(method))
            return false;

        if (arity != this.params.size())
            throw new JsonRpcException(String.format("Invalid number of parameters: expected %d found %d", arity, this.params.size()));

        return true;
    }

    public boolean check(String method, int fromArity, int toArity) throws JsonRpcException {
        if (!this.method.equals(method))
            return false;

        if (fromArity > this.params.size() || toArity < this.params.size())
            throw new JsonRpcException(String.format("Invalid number of parameters: expected %d thru %d found %d", fromArity, toArity, this.params.size()));

        return true;
    }
}
