package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.json.JsonConverter;
import com.ajlopez.blockchain.json.JsonStringValue;
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
    private final JsonValue error;

    public static JsonRpcResponse createResponseWithError(JsonRpcRequest request, Exception ex) {
        JsonValue error = new JsonStringValue(ex.getMessage());

        return new JsonRpcResponse(request.getId(), request.getJsonRpc(), null, error);
    }

    public static JsonRpcResponse createResponse(JsonRpcRequest request, int result) {
        JsonValue value = JsonConverter.convert(HexUtils.bytesToHexString(ByteUtils.unsignedIntegerToNormalizedBytes(result), true, true));

        return createResponse(request, value);
    }

    public static JsonRpcResponse createResponse(JsonRpcRequest request, long result) {
        JsonValue value = JsonConverter.convert(HexUtils.bytesToHexString(ByteUtils.unsignedLongToNormalizedBytes(result), true, true));

        return createResponse(request, value);
    }

    public static JsonRpcResponse createResponse(JsonRpcRequest request, String result) {
        JsonValue value = JsonConverter.convert(result);

        return createResponse(request, value);
    }

    public static JsonRpcResponse createResponse(JsonRpcRequest request, JsonValue result) {
        return new JsonRpcResponse(request.getId(), request.getJsonRpc(), result, null);
    }

    public JsonRpcResponse(String id, String jsonrpc, JsonValue result, JsonValue error) {
        this.id = id;
        this.jsonrpc = jsonrpc;
        this.result = result;
        this.error = error;
    }

    public String getId() { return this.id; }

    public String getJsonRpc() { return this.jsonrpc; }

    public JsonValue getResult() { return this.result; }

    public JsonValue getError() { return this.error; }
}