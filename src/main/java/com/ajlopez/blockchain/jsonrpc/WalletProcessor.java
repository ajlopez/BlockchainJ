package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.json.JsonArrayValue;

import java.util.Collections;

/**
 * Created by ajlopez on 01/12/2018.
 */
public class WalletProcessor implements JsonRpcProcessor {
    @Override
    public JsonRpcResponse processRequest(JsonRpcRequest request) throws JsonRpcException {
        return JsonRpcResponse.createResponse(request, new JsonArrayValue(Collections.emptyList()));
    }
}
