package com.ajlopez.blockchain.jsonrpc;

import com.ajlopez.blockchain.json.JsonNullValue;

/**
 * Created by ajlopez on 20/08/2019.
 */
public class TransactionsProcessor extends AbstractJsonRpcProcessor {
    private final TransactionsProvider transactionsProvider;

    public TransactionsProcessor(TransactionsProvider transactionsProvider) {
        this.transactionsProvider = transactionsProvider;
    }

    @Override
    public JsonRpcResponse processRequest(JsonRpcRequest request) throws JsonRpcException {
        return JsonRpcResponse.createResponse(request, JsonNullValue.getInstance());
    }
}
