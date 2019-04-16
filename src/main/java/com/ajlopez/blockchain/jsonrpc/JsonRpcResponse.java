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
    private final String jsonrpc;
    private final JsonValue result;

    public static JsonRpcResponse createResponse(JsonRpcRequest request, int result) {
        JsonValue value = JsonConverter.convert(HexUtils.bytesToHexString(ByteUtils.unsignedIntegerToNormalizedBytes(result), true));

        return createResponse(request, value);
    }

    public static JsonRpcResponse createResponse(JsonRpcRequest request, long result) {
        JsonValue value = JsonConverter.convert(HexUtils.bytesToHexString(ByteUtils.unsignedLongToNormalizedBytes(result), true));

        return createResponse(request, value);
    }

    public static JsonRpcResponse createResponse(JsonRpcRequest request, String result) {
        JsonValue value = JsonConverter.convert(result);

        return createResponse(request, value);
    }

    public static JsonRpcResponse createResponse(JsonRpcRequest request, JsonValue result) {
        return new JsonRpcResponse(request.getId(), request.getJsonRpc(), result);
    }

    public JsonRpcResponse(String id, String jsonrpc, JsonValue result) {
        this.id = id;
        this.jsonrpc = jsonrpc;
        this.result = result;
    }

    public String getId() { return this.id; }

    public String getJsonRpc() { return this.jsonrpc; }

    public JsonValue getResult() { return this.result; }
}