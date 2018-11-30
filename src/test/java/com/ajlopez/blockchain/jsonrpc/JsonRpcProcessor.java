package com.ajlopez.blockchain.jsonrpc;

/**
 * Created by ajlopez on 30/11/2018.
 */
public interface JsonRpcProcessor {
    JsonRpcResponse processRequest(JsonRpcRequest request);
}
