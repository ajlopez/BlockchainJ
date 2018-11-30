package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.json.JsonValue;

/**
 * Created by ajlopez on 11/30/2018.
 */
public class JsonRpcResponse {
    private final String id;
    private final String version;
    private final JsonValue result;

    public JsonRpcResponse(String id, String version, JsonValue result) {
        this.id = id;
        this.version = version;
        this.result = result;
    }

    public String getId() { return this.id; }

    public String getVersion() { return this.version; }

    public JsonValue getResult() { return this.result; }
}