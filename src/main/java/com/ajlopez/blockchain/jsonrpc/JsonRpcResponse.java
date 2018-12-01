package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.json.JsonConverter;
import com.ajlopez.blockchain.json.JsonValue;
import com.ajlopez.blockchain.utils.ByteUtils;
import com.ajlopez.blockchain.utils.HexUtils;

/**
 * Created by ajlopez on 11/30/2018.
 */
public class JsonRpcResponse {
    private final String id;
    private final String version;
    private final JsonValue result;

    public static JsonRpcResponse createResponse(JsonRpcRequest request, long result) {
        JsonValue value = JsonConverter.convert(HexUtils.bytesToHexString(ByteUtils.unsignedLongToNormalizedBytes(result), true));

        return createResponse(request, value);
    }

    public static JsonRpcResponse createResponse(JsonRpcRequest request, JsonValue result) {
        return new JsonRpcResponse(request.getId(), request.getVersion(), result);
    }

    public JsonRpcResponse(String id, String version, JsonValue result) {
        this.id = id;
        this.version = version;
        this.result = result;
    }

    public String getId() { return this.id; }

    public String getVersion() { return this.version; }

    public JsonValue getResult() { return this.result; }
}