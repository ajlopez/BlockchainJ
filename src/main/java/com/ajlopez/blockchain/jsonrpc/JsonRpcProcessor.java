package com.ajlopez.blockchain.jsonrpc;

import java.io.IOException;

/**
 * Created by ajlopez on 30/11/2018.
 */
public interface JsonRpcProcessor {
    JsonRpcResponse processRequest(JsonRpcRequest request) throws JsonRpcException, IOException;
}
