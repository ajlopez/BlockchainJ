package com.ajlopez.blockchain.jsonrpc;

import java.io.IOException;

/**
 * Created by ajlopez on 01/12/2018.
 */
public abstract class AbstractJsonRpcProcessor implements JsonRpcProcessor {
    public JsonRpcResponse processRequest(JsonRpcRequest request) throws JsonRpcException, IOException {
        throw new JsonRpcException(String.format("Unknown method '%s'", request.getMethod()));
    }
}
